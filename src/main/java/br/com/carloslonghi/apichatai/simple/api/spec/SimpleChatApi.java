package br.com.carloslonghi.apichatai.simple.api.spec;

import br.com.carloslonghi.apichatai.simple.dto.request.SimpleChatRequest;
import br.com.carloslonghi.apichatai.simple.dto.response.SimpleChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Chat simples",
        description = "Chamada única ao modelo, sem histórico"
)
public interface SimpleChatApi {

    @Operation(
            summary = "Enviar mensagem ao chat simples",
            description = "Envia uma mensagem isolada ao modelo e retorna a resposta, sem persistir histórico"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resposta gerada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimpleChatResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados da request inválidos", content = @Content)
    })
    ResponseEntity<SimpleChatResponse> simpleChat(
            @RequestBody(
                    description = "Mensagem a ser enviada ao modelo",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SimpleChatRequest.class))
            )
            SimpleChatRequest message
    );
}
