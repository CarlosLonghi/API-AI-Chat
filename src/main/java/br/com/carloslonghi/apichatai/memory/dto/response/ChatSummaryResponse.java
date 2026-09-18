package br.com.carloslonghi.apichatai.memory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo de um chat com memória, para listagem")
public record ChatSummaryResponse(
        @Schema(description = "Identificador do chat (conversation_id)", example = "69b6a32b-b2df-4192-8878-5749e520dc44")
        String id,

        @Schema(description = "Título curto gerado automaticamente para o chat", example = "Resumo do jogo Expedition 33")
        String description
) {
}
