package br.com.carloslonghi.apichatai.memory;

import br.com.carloslonghi.apichatai.memory.api.spec.MemoryChatApi;
import br.com.carloslonghi.apichatai.memory.dto.request.ChatMessageRequest;
import br.com.carloslonghi.apichatai.memory.dto.request.UpdateChatDescriptionRequest;
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
public class MemoryChatController implements MemoryChatApi {

    private final MemoryChatService memoryChatService;

    public MemoryChatController(MemoryChatService memoryChatService) {
        this.memoryChatService = memoryChatService;
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<ChatReplyResponse> continueChat(@PathVariable UUID chatId, @Valid @RequestBody ChatMessageRequest message) {
        String response = this.memoryChatService.sendMessage(message.message(), chatId);
        return ResponseEntity.ok(new ChatReplyResponse(response));
    }

    @PostMapping("/new")
    public ResponseEntity<NewChatResponse> newChat(@Valid @RequestBody ChatMessageRequest message) {
        NewChatResponse response = this.memoryChatService.createChat(message.message());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatSummaryResponse>> getAllChats() {
        return ResponseEntity.ok(this.memoryChatService.getAllChatsByUser());
    }

    @PatchMapping("/{chatId}")
    public ResponseEntity<ChatSummaryResponse> updateChatDescription(@PathVariable UUID chatId, @Valid @RequestBody UpdateChatDescriptionRequest request) {
        return ResponseEntity.ok(this.memoryChatService.updateChatDescription(chatId, request.description()));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<ChatHistoryResponse>> getChatMessages(@PathVariable UUID chatId) {
        return ResponseEntity.ok(this.memoryChatService.getChatMessages(chatId));
    }
}
