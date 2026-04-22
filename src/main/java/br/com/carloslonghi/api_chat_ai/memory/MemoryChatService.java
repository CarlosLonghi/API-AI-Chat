package br.com.carloslonghi.api_chat_ai.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class MemoryChatService {

    private final ChatClient chatClient;

    public MemoryChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    String sendMessage(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
