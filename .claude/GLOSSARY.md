# Glossary — Domain Terminology

Deeper-dive reference for the domain model summarized in the root `CLAUDE.md`. Read that first; come here when you need term-level detail.

## Core concepts

### Simple chat
`simple/` — a single, stateless request/response cycle with the model (`SimpleChatController` → `SimpleChatService` → `ChatClient`). No `chatId`, no persistence, no history sent to the model beyond the one message in the request.

### Memory chat
`memory/` — a multi-turn conversation identified by a `chatId` (`conversation_id`). Backed by two separate tables (see below) and a `MessageWindowChatMemory` that limits how much history is sent back to the model on each turn (currently the last 10 messages), even though the full history stays in the database.

### `chat_memory` (table)
This project's own metadata table (`schema-postgresql.sql`): `conversation_id` (`UUID`, PK, default `gen_random_uuid()`), `user_id`, `description`. Written by `MemoryChatRepository.generateChatId` when a chat is created — before the first message is ever sent. Read by `getAllChatsByUser` (chat list) and by `existsChat` (existence check used to reject `POST /{chatId}` for an unknown chat, `[[architecture]]` invariant I4).

### `spring_ai_chat_memory` (table)
Owned and written by Spring AI's `JdbcChatMemoryRepository` (`spring-ai-starter-model-chat-memory-repository-jdbc`), not by this project's code directly: `conversation_id`, `content`, `type` (`USER`/`ASSISTANT`/`SYSTEM`/`TOOL`), `timestamp`. A row is only inserted when the `MessageChatMemoryAdvisor` actually persists a message as part of a `ChatClient` call — **not** when a chat is merely created in `chat_memory`. `MemoryChatRepository.getChatMessages` reads this table directly with `JdbcTemplate` to build the chat history response.

### `existsChat`
`MemoryChatRepository.existsChat(chatId)` — must query `chat_memory`, since that's the table populated at chat-creation time. Querying `spring_ai_chat_memory` instead was a real bug: a brand-new chat has no row there yet (no message has been sent), so `MemoryChatService.createChat` → `sendMessage` → `existsChat` would throw `ChatNotFoundException` on the chat's own creation. Fixed; don't reintroduce by "simplifying" the two tables into one check.

### `MessageWindowChatMemory`
Spring AI class configured in `MemoryChatService` with `maxMessages(10)` — caps how many past messages are included in the prompt sent to the model. Independent from how much history is stored (all of it) or returned by `GET /{chatId}` (all of it too).

### Advisors (`ChatClient`)
- `MessageChatMemoryAdvisor` — injects the relevant `ChatMemory` window into the prompt and persists new turns to `spring_ai_chat_memory`. Only registered on the `memory/` `ChatClient` (`MemoryChatService`), not on `simple/`'s.
- `SimpleLoggerAdvisor` — logs prompt/response for debugging; see `logging.level.org.springframework.ai.chat.client.advisor: DEBUG` in `application.yaml`.

### Description generation
`MemoryChatService.generateChatDescription` — a dedicated prompt (`DESCRIPTION_PROMPT`) asks the model for a short pt-BR title (≤30 chars) for a new chat; the result is trimmed/truncated defensively and falls back to `"Nova conversa"` if the model returns nothing. Stored in `chat_memory.description`, surfaced via `ChatSummaryResponse`.

### `DEFAULT_USER_ID`
`MemoryChatService.DEFAULT_USER_ID = "carlos"` — every chat belongs to this hardcoded user; there's no Spring Security integration yet (documented as a `TODO` in the code itself). Not a bug, but a known limitation — see `[[architecture]]` invariant I6.

### `ChatClient` / `ChatClient.Builder`
Spring AI's fluent entry point to the LLM. `simple/` builds a plain one; `memory/` attaches `MessageChatMemoryAdvisor` + `SimpleLoggerAdvisor` via `defaultAdvisors(...)`. Both ultimately talk to whatever OpenAI-compatible endpoint is configured (`spring.ai.openai.*` in `application.yaml` — Groq today, via `GROQ_AI_URL`/`GROQ_AI_API_KEY`/`GROQ_AI_MODEL`).

## Quick lookups

| Term | File |
|------|------|
| Stateless chat endpoint | `simple/SimpleChatController.java` |
| Multi-turn chat endpoints | `memory/MemoryChatController.java` |
| Chat metadata table access | `memory/MemoryChatRepository.java` |
| Chat memory window size (10) | `memory/MemoryChatService.java` |
| Description prompt | `memory/MemoryChatService.java` (`DESCRIPTION_PROMPT`) |
| Global error → HTTP mapping | `config/GlobalExceptionHandler.java` |
| LLM connection config | `src/main/resources/application.yaml`, `application.env.example` |
| DB schema (Postgres / MySQL) | `src/main/resources/schema-postgresql.sql`, `schema-mysql.sql` |
