package br.com.carloslonghi.apichatai.memory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da criação de um novo chat com memória")
public record NewChatResponse(
        @Schema(description = "Identificador do chat criado (conversation_id)", example = "69b6a32b-b2df-4192-8878-5749e520dc44")
        String chatId,

        @Schema(description = "Título curto gerado automaticamente para o chat", example = "Resumo do jogo Expedition 33")
        String description,

        @Schema(description = "Resposta do modelo para a primeira mensagem", example = "Expedition 33 é um RPG...")
        String response
) {
}
