package br.com.carloslonghi.apichatai.memory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Corpo da requisição para alterar o título de um chat")
public record UpdateChatDescriptionRequest(
        @Schema(description = "Novo título do chat (máx. 30 caracteres)", example = "Dicas de Spring Boot", maxLength = 30)
        @NotBlank
        @Size(max = 30)
        String description
) {
}
