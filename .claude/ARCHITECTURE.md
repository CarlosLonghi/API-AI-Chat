# Architecture — Module Map & Invariants

Deeper-dive reference for the layered architecture summarized in the root `CLAUDE.md`. Read that first; come here for the full module map and the invariants list.

## Directory structure

```
src/main/java/br/com/carloslonghi/apichatai/
├── ApiChatAiApplication.java
│
├── config/
│   ├── AIConfig.java                 # RestClientCustomizer — read timeout (60s) for the Spring AI HTTP client
│   └── GlobalExceptionHandler.java   # @RestControllerAdvice — ChatNotFoundException → 404, validation → 400, fallback → 500
│
├── simple/
│   ├── SimpleChatController.java     # POST /api/v1/chat/simple — stateless, single turn
│   ├── SimpleChatService.java        # ChatClient.prompt().user(...).call()
│   └── dto/
│       ├── request/SimpleChatRequest.java   # @NotBlank message
│       └── response/SimpleChatResponse.java
│
└── memory/
    ├── MemoryChatController.java     # /api/v1/chat/memory — multi-turn, chatId-scoped
    ├── MemoryChatService.java        # ChatClient with ChatMemory advisor; owns DEFAULT_USER_ID and description generation
    ├── MemoryChatRepository.java     # JdbcTemplate — chat_memory table (metadata) + reads from spring_ai_chat_memory (Spring AI's own table)
    ├── ChatNotFoundException.java
    └── dto/
        ├── request/ChatMessageRequest.java          # @NotBlank message
        └── response/
            ├── ChatHistoryResponse.java              # one row of spring_ai_chat_memory
            ├── ChatReplyResponse.java
            ├── ChatSummaryResponse.java               # one row of chat_memory (id + description)
            └── NewChatResponse.java

src/main/resources/
├── application.yaml
├── application.env.example         # documents GROQ_AI_API_KEY / GROQ_AI_URL / GROQ_AI_MODEL
├── schema-postgresql.sql           # chat_memory + SPRING_AI_CHAT_MEMORY (active datasource)
└── schema-mysql.sql                # SPRING_AI_CHAT_MEMORY only, for the MySQL alternative
```

## Module responsibilities

**config/** — Cross-cutting, global policy. `GlobalExceptionHandler` is the only place HTTP status codes get decided for errors; don't scatter `ResponseStatusException` across services.

**simple/** — No persistence, no `chatId`. Each request is an independent call to the model. Keep it that way — if a caller needs history, they should use `memory/`, not a parameter bolted onto `simple/`.

**memory/** — Owns two tables (see Domain model in root `CLAUDE.md`): `chat_memory` (this project's own metadata table, written by `MemoryChatRepository`) and `spring_ai_chat_memory` (owned by Spring AI's `JdbcChatMemoryRepository`, written by the `MessageChatMemoryAdvisor` when a prompt is sent). `MemoryChatService` is the only place that talks to both the `ChatClient` and `MemoryChatRepository` — controllers never touch `MemoryChatRepository` directly.

## Architectural invariants

- **I1 — Controllers never build SQL or call `ChatClient` directly.** Always go through a `*Service`. A controller method with a `JdbcTemplate` or `ChatClient` field is a bug.
- **I2 — `simple/` and `memory/` DTOs are request/response pairs, never shared.** A single record used for both directions (like the old `SimpleChatDTO`) is a smell — split it into `dto/request/*Request` and `dto/response/*Response`, mirroring `memory/dto/*`.
- **I3 — Request validation lives on the DTO, not in the service.** `@NotBlank`/Bean Validation annotations on the record field, `@Valid @RequestBody` on the controller parameter. `GlobalExceptionHandler` translates `MethodArgumentNotValidException` → 400 with a field-error map.
- **I4 — A new memory chat's existence check must read `chat_memory`, not `spring_ai_chat_memory`.** `chat_memory` gets its row in `MemoryChatRepository.generateChatId`, before the first message is ever sent; `spring_ai_chat_memory` only gets a row once the `MessageChatMemoryAdvisor` actually persists a message. Checking the wrong table makes a brand-new chat 404 on its own creation (this was a real bug — see `[[glossary]]` "existsChat"). `chatId` is a `UUID` end to end in `memory/` (`@PathVariable UUID`, `MemoryChatService`, `existsChat`/`generateChatId`) — the column is `UUID` in Postgres and `uuid = character varying` has no implicit cast (`PSQLException`). `getChatMessages` is the one exception, taking `String`, because `spring_ai_chat_memory.conversation_id` is `VARCHAR(36)`; `MemoryChatService` bridges with `chatId.toString()`. An invalid `chatId` in the URL is rejected at the web layer as `MethodArgumentTypeMismatchException` → 400 (`GlobalExceptionHandler`), before it reaches the service/repository.
- **I5 — Creating a resource returns `201 Created`.** `POST /api/v1/chat/memory/new` returns `ResponseEntity<NewChatResponse>` with `201`. `POST /{chatId}` (continuing an existing chat) stays `200` — it doesn't create anything.
- **I6 — `MemoryChatService.DEFAULT_USER_ID` is a known, documented placeholder.** There is no Spring Security integration yet; every chat belongs to the same hardcoded user. Don't work around this with per-request tricks — when auth lands, thread the real user id from `SecurityContextHolder`/`@AuthenticationPrincipal` through `MemoryChatService` → `MemoryChatRepository`.
- **I7 — No JPA/Hibernate.** Persistence is `JdbcTemplate` (this project's `chat_memory` table) plus whatever Spring AI's `JdbcChatMemoryRepository` manages (`spring_ai_chat_memory`). Don't add `spring-boot-starter-data-jpa`/entities for this without a real reason — the schema is intentionally minimal and managed by hand (`schema-*.sql`), not Flyway/Liquibase.

## Data flow

```
HTTP Request
    → [Controller] @Valid RequestDTO
    → [Service] business rules (description generation, existence check, DEFAULT_USER_ID)
    → [ChatClient] (+ ChatMemory advisor for memory/) → LLM provider (OpenAI-compatible, Groq today)
    → [Repository] JdbcTemplate (chat_memory) / JdbcChatMemoryRepository (spring_ai_chat_memory)
    → [Service] plain String / response record
    → [Controller] ResponseEntity / response record
    → [GlobalExceptionHandler] exception → ProblemDetail
HTTP Response (200/201/400/404/500)
```

## Adding a new feature — checklist

1. **Decide simple vs. memory** — does it need conversation history? If not, keep it in (or alongside) `simple/`; don't add persistence to a stateless flow.
2. **Request/response DTOs** — new records under the feature's `dto/{request,response}`, with validation annotations where needed.
3. **Service** — business rules; keep HTTP concerns (status codes) out, let the controller/`GlobalExceptionHandler` decide those.
4. **Controller** — `@Valid @RequestBody`, correct HTTP status for the operation (`201` for creation, `200` otherwise).
5. **Schema** — if a new table/column is needed, update both `schema-postgresql.sql` and `schema-mysql.sql` (there's no migration tool here — keep them in sync by hand).
6. **Compile & test** — `./mvnw compile`, `./mvnw test`.

For anything crossing both `simple/` and `memory/`, a schema change, or introducing auth, consider writing an ExecPlan first — see `[[plans]]`.
