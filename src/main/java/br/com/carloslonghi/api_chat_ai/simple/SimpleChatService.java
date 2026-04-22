package br.com.carloslonghi.api_chat_ai.simple;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SimpleChatService {

    private final ChatClient chatClient;

    public SimpleChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String sendMessage(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
