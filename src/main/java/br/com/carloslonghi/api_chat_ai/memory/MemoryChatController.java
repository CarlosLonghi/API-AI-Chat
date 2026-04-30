package br.com.carloslonghi.api_chat_ai.memory;

import br.com.carloslonghi.api_chat_ai.memory.dto.request.ChatMessageRequest;
import br.com.carloslonghi.api_chat_ai.memory.dto.response.ChatHistoryResponse;
import br.com.carloslonghi.api_chat_ai.memory.dto.response.ChatReplyResponse;
import br.com.carloslonghi.api_chat_ai.memory.dto.response.ChatSummaryResponse;
import br.com.carloslonghi.api_chat_ai.memory.dto.response.NewChatResponse;
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
    ChatReplyResponse continueChat(@PathVariable String chatId, @RequestBody ChatMessageRequest message) {
        String response = this.memoryChatService.sendMessage(message.message(), chatId);
        return new ChatReplyResponse(response);
    }

    @PostMapping("/new")
    NewChatResponse newChat(@RequestBody ChatMessageRequest message) {
        return this.memoryChatService.createChat(message.message());
    }

    @GetMapping
    List<ChatSummaryResponse> getAllChats() {
        return this.memoryChatService.getAllChatsByUser();
    }

    @GetMapping("/{chatId}")
    List<ChatHistoryResponse> getChatMessages(@PathVariable String chatId) {
        return this.memoryChatService.getChatMessages(chatId);
    }
}
