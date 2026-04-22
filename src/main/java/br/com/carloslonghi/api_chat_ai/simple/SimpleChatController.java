package br.com.carloslonghi.api_chat_ai.simple;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/simple")
public class SimpleChatController {

    private final SimpleChatService simpleChatService;

    public SimpleChatController(SimpleChatService simpleChatService) {
        this.simpleChatService = simpleChatService;
    }

    @PostMapping
    SimpleChatDTO simpleChat(@RequestBody SimpleChatDTO message) {
        String response = simpleChatService.sendMessage(message.message());

        return new SimpleChatDTO(response);
    }
}
