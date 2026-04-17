package br.com.carloslonghi.api_chat_ai.controller;

import br.com.carloslonghi.api_chat_ai.dto.ChatDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping
    ChatDTO simpleChat(@RequestBody ChatDTO message) {
        String response = this.chatClient.prompt()
                .user(message.message())
                .call()
                .content();
        return new ChatDTO(response);
    }
}
