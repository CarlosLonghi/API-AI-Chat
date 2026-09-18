# API Chat AI

Java 21 + Spring Boot 4 REST API que expõe chat com um LLM via Spring AI (endpoint compatível com OpenAI — hoje configurado para Groq). Dois modos de chat, sem sobreposição de código: **simple** (uma chamada, sem histórico) e **memory** (histórico persistido, multi-turno). Package root: `br.com.carloslonghi.apichatai`.

## Build / run / test

- Run locally: `./mvnw spring-boot:run` (lê `src/main/resources/application.yaml`, que resolve `${GROQ_AI_API_KEY}`, `${GROQ_AI_URL}`, `${GROQ_AI_MODEL}` do ambiente — ver `application.env.example`).
- Compile only: `./mvnw -q -DskipTests compile`.
- Tests: `./mvnw test`.
- `docker compose up -d` sobe **apenas** o Postgres (`postgres:latest`, DB `mydatabase`); a aplicação em si não é containerizada, roda com o comando Maven acima.
- Schema manual — não há Flyway/Liquibase. Rode `src/main/resources/schema-postgresql.sql` (padrão) ou `schema-mysql.sql` (se trocar o datasource) diretamente no banco antes de subir a aplicação. `spring.jpa.hibernate.ddl-auto` não se aplica: o projeto não usa JPA/Hibernate (não há dependência `spring-boot-starter-data-jpa`), só `JdbcTemplate` + o `JdbcChatMemoryRepository` do Spring AI.

## Commit convention

- **Semantic commits** (Conventional Commits): `feat:`, `fix:`, `chore:`, `docs:`, `test:`, `refactor:`, `build:`, `ci:`. Subject line no imperativo e conciso.
- **Divida o trabalho em commits menores e coesos** — nunca um commit único com a feature inteira. Cada commit deve fazer sentido isolado (ex.: rename de pacote, fix de bug, DTOs, docs).
- **Não adicione créditos de IA** nos commits: sem `Co-Authored-By: Claude`, `Generated with Claude Code`, `Claude-Session:` ou trailers equivalentes — nem em commits, nem em PRs.
- O corpo do commit (quando útil) explica o *porquê*, não repete o diff.

## Arquitetura em pacotes por feature

Cada modo de chat é um pacote próprio e autocontido — não há camada `controller`/`service`/`repository` compartilhada entre `simple` e `memory`:

- `simple/` — `SimpleChatController` → `SimpleChatService` → `ChatClient` (sem persistência). DTOs em `simple/dto/{request,response}`.
- `memory/` — `MemoryChatController` → `MemoryChatService` → `ChatClient` (com `ChatMemory`) + `MemoryChatRepository` (`JdbcTemplate`, tabela `chat_memory`). DTOs em `memory/dto/{request,response}`. Exceção de domínio `ChatNotFoundException`.
- `config/` — `AIConfig` (timeout do `RestClient` usado pelo Spring AI), `GlobalExceptionHandler` (`@RestControllerAdvice` global).
- Controllers nunca lidam com SQL/`ChatClient` diretamente — sempre via um `*Service`. Requests validam com Bean Validation (`@NotBlank` em `message`) e os controllers usam `@Valid @RequestBody`.
- Endpoints que criam um recurso (novo chat) retornam `201 Created`.

## Modelo de domínio

- **Simple chat** (`POST /api/v1/chat/simple`) — sem estado, sem `chatId`: cada chamada é uma conversa isolada com o modelo. Não passa por nenhuma tabela.
- **Memory chat** (`/api/v1/chat/memory`) — conversa com histórico, identificada por `chatId` (`conversation_id`, `UUID`):
  - `POST /new` — cria um chat: gera uma descrição curta (prompt dedicado, `DESCRIPTION_PROMPT`, truncada em 30 caracteres), insere a linha de metadados em `chat_memory` (`generateChatId`) e envia a primeira mensagem. Retorna `201` com `chatId`, `description` e a resposta do modelo.
  - `POST /{chatId}` — continua um chat existente. Valida a existência do chat (`existsChat`, contra `chat_memory` — ver Gotchas) antes de chamar o modelo; se não existir, `ChatNotFoundException` → 404.
  - `GET` — lista os chats do usuário (`chat_memory`, filtrado por `user_id`).
  - `GET /{chatId}` — histórico de mensagens (tabela `spring_ai_chat_memory`, gerenciada pelo Spring AI via `JdbcChatMemoryRepository`).
  - **Duas tabelas distintas**: `chat_memory` é uma tabela própria do projeto (metadados: `conversation_id`, `user_id`, `description`); `spring_ai_chat_memory` é gerenciada pelo `JdbcChatMemoryRepository` do Spring AI (mensagens: `conversation_id`, `content`, `type`, `timestamp`). `MessageWindowChatMemory` limita o contexto enviado ao modelo às últimas 10 mensagens (`MemoryChatService`), mas o histórico completo continua no banco.
  - Advisors do `ChatClient` (`MemoryChatService`): `MessageChatMemoryAdvisor` (injeta histórico) + `SimpleLoggerAdvisor` (debug, ver `logging.level` no `application.yaml`).

## Gotchas

- **`DEFAULT_USER_ID` é fixo (`"carlos"`)** em `MemoryChatService` — já documentado como TODO no próprio código: hoje não há autenticação, todo chat pertence ao mesmo usuário. Ao integrar Spring Security, extrair o usuário real do `SecurityContextHolder`/`@AuthenticationPrincipal` e propagar para `MemoryChatRepository`.
- **`existsChat` deve consultar `chat_memory`, não `spring_ai_chat_memory`.** A tabela `spring_ai_chat_memory` só recebe linhas depois que uma mensagem é de fato enviada pelo advisor; `chat_memory` já tem a linha assim que `generateChatId` roda. Checar a tabela errada faz `POST /new` falhar com 404 na própria criação do chat (bug já corrigido — não reintroduzir).
- **`chat_memory.conversation_id` é `UUID` no Postgres, não `VARCHAR`.** O driver JDBC do Postgres não compara `uuid = varchar` implicitamente (`operator does not exist: uuid = character varying`) — passar o `chatId` como `String` direto pro `?` de uma query contra `chat_memory` quebra com `BadSqlGrammarException`. `existsChat` primeiro converte para `UUID.fromString(chatId)` (e trata `IllegalArgumentException` como "não existe", não como erro 500) antes de passar pro `JdbcTemplate`. Qualquer nova query contra `chat_memory.conversation_id` precisa do mesmo cuidado.
- **Suporte dual Postgres/MySQL é manual**: `schema-postgresql.sql` e `schema-mysql.sql` coexistem; só um datasource fica ativo por vez em `application.yaml` (o bloco MySQL fica comentado como referência). Trocar de banco exige descomentar o bloco certo e rodar o schema correspondente à mão.
- Docker Compose só sobe o Postgres — a aplicação Spring Boot roda localmente com `./mvnw spring-boot:run`.
- `spring-ai.version` é `2.0.0-M4` (milestone) — checar o repositório de milestones do Spring caso `mvn` não resolva a dependência num ambiente novo.

## Próxima etapa (fora deste escopo)

Documentação Swagger/OpenAPI (`springdoc-openapi`) ainda não foi adicionada — fica para uma próxima rodada.
