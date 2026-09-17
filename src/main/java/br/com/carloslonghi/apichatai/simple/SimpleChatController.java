package br.com.carloslonghi.apichatai.simple;

import br.com.carloslonghi.apichatai.simple.dto.request.SimpleChatRequest;
import br.com.carloslonghi.apichatai.simple.dto.response.SimpleChatResponse;
import jakarta.validation.Valid;
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
    public SimpleChatResponse simpleChat(@Valid @RequestBody SimpleChatRequest message) {
        String response = simpleChatService.sendMessage(message.message());

        return new SimpleChatResponse(response);
    }
}
