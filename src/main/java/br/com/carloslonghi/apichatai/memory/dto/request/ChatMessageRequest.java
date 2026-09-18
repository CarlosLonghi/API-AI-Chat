package br.com.carloslonghi.apichatai.memory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Corpo da requisição para enviar uma mensagem a um chat")
public record ChatMessageRequest(
        @Schema(description = "Mensagem do usuário", example = "Qual a capital do Brasil?")
        @NotBlank
        String message
) {
}
