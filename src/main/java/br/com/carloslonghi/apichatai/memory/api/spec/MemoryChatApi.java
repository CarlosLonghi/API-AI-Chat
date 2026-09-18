package br.com.carloslonghi.apichatai.memory.api.spec;

import br.com.carloslonghi.apichatai.memory.dto.request.ChatMessageRequest;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatHistoryResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatReplyResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.ChatSummaryResponse;
import br.com.carloslonghi.apichatai.memory.dto.response.NewChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Chat com memória",
        description = "Chat multi-turno com histórico persistido, identificado por um chatId"
)
public interface MemoryChatApi {

    @Operation(
            summary = "Criar novo chat",
            description = "Cria um chat novo, gera um título curto automaticamente para ele e envia a primeira mensagem ao modelo"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Chat criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NewChatResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados da request inválidos", content = @Content)
    })
    ResponseEntity<NewChatResponse> newChat(
            @RequestBody(
                    description = "Primeira mensagem do chat",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatMessageRequest.class))
            )
            ChatMessageRequest message
    );

    @Operation(
            summary = "Continuar um chat existente",
            description = "Envia uma nova mensagem para um chat já criado, usando o histórico persistido"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resposta gerada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatReplyResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados da request inválidos ou chatId mal formado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Chat não encontrado", content = @Content)
    })
    ResponseEntity<ChatReplyResponse> continueChat(
            @Parameter(in = ParameterIn.PATH, description = "Identificador do chat", required = true)
            @PathVariable UUID chatId,
            @RequestBody(
                    description = "Mensagem a ser enviada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatMessageRequest.class))
            )
            ChatMessageRequest message
    );

    @Operation(
            summary = "Listar chats",
            description = "Retorna o resumo (id e título) de todos os chats do usuário"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatSummaryResponse.class))
                    )
            )
    })
    ResponseEntity<List<ChatSummaryResponse>> getAllChats();

    @Operation(
            summary = "Histórico de um chat",
            description = "Retorna todas as mensagens já trocadas em um chat, em ordem cronológica"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Histórico retornado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChatHistoryResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "chatId mal formado", content = @Content)
    })
    ResponseEntity<List<ChatHistoryResponse>> getChatMessages(
            @Parameter(in = ParameterIn.PATH, description = "Identificador do chat", required = true)
            @PathVariable UUID chatId
    );
}
