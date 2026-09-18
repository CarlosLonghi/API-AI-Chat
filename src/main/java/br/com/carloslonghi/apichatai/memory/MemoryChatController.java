package br.com.carloslonghi.apichatai.memory;

import br.com.carloslonghi.apichatai.memory.dto.request.ChatMessageRequest;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatHistoryResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatReplyResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatSummaryResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.NewChatResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat/memory")
public class MemoryChatController {

    private final MemoryChatService memoryChatService;

    public MemoryChatController(MemoryChatService memoryChatService) {
        this.memoryChatService = memoryChatService;
    }

    @PostMapping("/{chatId}")
    public ChatReplyResponse continueChat(@PathVariable UUID chatId, @Valid @RequestBody ChatMessageRequest message) {
        String response = this.memoryChatService.sendMessage(message.message(), chatId);
        return new ChatReplyResponse(response);
    }

    @PostMapping("/new")
    public ResponseEntity<NewChatResponse> newChat(@Valid @RequestBody ChatMessageRequest message) {
        NewChatResponse response = this.memoryChatService.createChat(message.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ChatSummaryResponse> getAllChats() {
        return this.memoryChatService.getAllChatsByUser();
    }

    @GetMapping("/{chatId}")
    public List<ChatHistoryResponse> getChatMessages(@PathVariable UUID chatId) {
        return this.memoryChatService.getChatMessages(chatId);
    }
}
