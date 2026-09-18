package br.com.carloslonghi.apichatai.simple.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Corpo da requisição para o chat simples (sem histórico)")
public record SimpleChatRequest(
        @Schema(description = "Mensagem do usuário", example = "Qual a capital do Brasil?")
        @NotBlank
        String message
) {
}
