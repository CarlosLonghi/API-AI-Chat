package br.com.carloslonghi.apichatai.memory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Uma mensagem do histórico de um chat com memória")
public record ChatHistoryResponse(
        @Schema(description = "Conteúdo da mensagem", example = "Qual a capital do Brasil?")
        String content,

        @Schema(description = "Tipo da mensagem", example = "USER", allowableValues = {"USER", "ASSISTANT", "SYSTEM", "TOOL"})
        String type,

        @Schema(description = "Momento em que a mensagem foi registrada", example = "2026-09-18 11:02:41.917")
        String timestamp
) {
}
