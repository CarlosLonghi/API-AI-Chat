package br.com.carloslonghi.api_chat_ai.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;

@Service
public class MemoryChatService {

    private final ChatClient chatClient;

    private final MemoryChatRepository memoryChatRepository;

    private static final String DEFAULT_USER_ID = "carlos";
    private static final String DESCRIPTION_PROMPT = "Crie um título curto (máx 30 caracteres) em pt-BR para este chat. Não use aspas. Seja ultra-direto. Se passar de 30 caracteres, você será penalizado. Mensagem: ";

    public MemoryChatService(ChatClient.Builder chatClientBuilder, JdbcChatMemoryRepository jdbcChatMemoryRepository, MemoryChatRepository memoryChatRepository) {
        this.memoryChatRepository = memoryChatRepository;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    String sendMessage(String message, String chatId) {
        return this.chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(message)
                .call()
                .content();
    }

    record NewChatResponse(String chatId, String description, String response) {}

    public NewChatResponse createChat(String message) {
        String description = generateChatDescription(message);
        String chatId = this.memoryChatRepository.generateChatId(DEFAULT_USER_ID, description);
        String response = this.sendMessage(message, chatId);
        return new NewChatResponse(chatId, description, response);
    }

    private String generateChatDescription(String message) {
        String description = this.chatClient.prompt()
                .user(DESCRIPTION_PROMPT + message)
                .call()
                .content();
        return description != null ? description.substring(0, Math.min(description.length(), 30)) : null;
    }
}
