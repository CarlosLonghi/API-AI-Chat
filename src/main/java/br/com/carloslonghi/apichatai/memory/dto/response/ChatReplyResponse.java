package br.com.carloslonghi.apichatai.memory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do modelo para uma mensagem enviada a um chat existente")
public record ChatReplyResponse(
        @Schema(description = "Resposta gerada pelo modelo", example = "A capital do Brasil é Brasília.")
        String message
) {
}
