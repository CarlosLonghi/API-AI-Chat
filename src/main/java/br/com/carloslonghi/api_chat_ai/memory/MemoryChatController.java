package br.com.carloslonghi.api_chat_ai.memory;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat/memory")
public class MemoryChatController {

    private final MemoryChatService memoryChatService;

    public MemoryChatController(MemoryChatService memoryChatService) {
        this.memoryChatService = memoryChatService;
    }

    @PostMapping("/{chatId}")
    MemoryChatDTO memoryChat(@PathVariable String chatId, @RequestBody MemoryChatDTO message) {
        String response = this.memoryChatService.sendMessage(message.message(), chatId);
        return new MemoryChatDTO(response);
    }

    @PostMapping("/new")
    NewChatResponse newChat(@RequestBody MemoryChatDTO message) {
        return this.memoryChatService.createChat(message.message());
    }

    @GetMapping
    List<ChatView> getAllChatsByUser() {
        return this.memoryChatService.getAllChatsByUser();
    }

    @GetMapping("/{chatId}")
    List<ChatMessage> getChatMessages(@PathVariable String chatId) {
        return this.memoryChatService.getChatMessages(chatId);
    }
}
