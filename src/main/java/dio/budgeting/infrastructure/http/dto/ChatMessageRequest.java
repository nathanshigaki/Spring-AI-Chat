package dio.budgeting.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Mensagem para interação textual com o Assistente Financeiro")
public record ChatMessageRequest(
        @NotBlank(message = "A mensagem não pode ser vazia")
        @Schema(description = "Comando ou pergunta em linguagem natural", example = "Gastei 55 reais na padaria hoje.")
        String message,

        @Schema(description = "Identificador opcional da sessão de conversa para manter memória de contexto", example = "sessao-usuario-123")
        String conversationId
) {}
