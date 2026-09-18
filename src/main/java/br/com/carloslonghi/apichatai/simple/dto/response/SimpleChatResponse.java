package br.com.carloslonghi.apichatai.simple.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do chat simples (sem histórico)")
public record SimpleChatResponse(
        @Schema(description = "Resposta gerada pelo modelo", example = "A capital do Brasil é Brasília.")
        String message
) {
}
