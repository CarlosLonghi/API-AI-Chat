package br.com.carloslonghi.api_chat_ai.memory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat/memory")
public class MemoryChatController {

    private final MemoryChatService memoryChatService;

    public MemoryChatController(MemoryChatService memoryChatService) {
        this.memoryChatService = memoryChatService;
    }

    @PostMapping
    MemoryChatDTO memoryChat(@RequestBody MemoryChatDTO message) {
        String response = this.memoryChatService.sendMessage(message.message());
        return new MemoryChatDTO(response);
    }
}
