# API-AI-Chat 🤖

Uma API REST moderna desenvolvida com **Spring Boot** e **Spring AI**, projetada para integrar funcionalidades de chat com modelos de Inteligência Artificial de forma escalável. O projeto utiliza a arquitetura **Package by Feature**, organizando o código em torno das funcionalidades de negócio.

## Tecnologias Utilizadas

*   **Java 21**
*   **Spring Boot 4**
*   **Spring AI**: Integração de alto nível com LLMs (Groq, OpenAI, etc).
*   **Docker & Docker Compose**: Gerenciamento de infraestrutura.
*   **Maven**: Automação de build.

## Estrutura do Projeto (Package by Feature)

A organização do código foca na modularidade por funcionalidade:

- `memory`: Gerenciamento de conversas com histórico e contexto.
- `simple`: Interações de chat diretas e sem persistência de estado.

## Endpoints da API

### 1. Chat Simples (Simple Chat)
Interação direta sem memória de contexto.
- **POST** `/api/v1/chat/simple`
  - **Body:** `{ "message": "Sua mensagem aqui" }`

### 2. Chat com Memória (Memory Chat)
Gerenciamento de conversas persistentes.

- **POST** `/api/v1/chat/memory/new`
  - Inicia uma nova conversa e retorna o `chatId`.
  - **Body:** `{ "message": "Início da conversa" }`

- **POST** `/api/v1/chat/memory/{chatId}`
  - Continua uma conversa existente mantendo o contexto.
  - **Body:** `{ "message": "Pergunta seguinte" }`

- **GET** `/api/v1/chat/memory`
  - Lista o resumo de todos os chats do usuário.

- **GET** `/api/v1/chat/memory/{chatId}`
  - Recupera o histórico completo de mensagens de um chat específico.

## Como Executar?

### 1. Clonar o Repositório
```bash
git clone https://github.com
cd API-AI-Chat
```

### 2. Configurar Variáveis de Ambiente
Renomeie o arquivo de exemplo em `src/main/resources` e adicione sua chave:
- Arquivo: `application.env`
- Propriedade: `GROQ_API_KEY=sua_chave_aqui`

### 3. Rodar a Aplicação
Via Maven:
```bash
./mvnw spring-boot:run
```
Ou via Docker:
```bash
docker-compose up
```

## Autor

*   **Carlos Longhi** - [GitHub](https://github.com/CarlosLonghi)

---
Desenvolvido para explorar o potencial do Spring AI em aplicações Java modernas.
