package dio.budgeting.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do Assistente Financeiro Inteligente")
public record ChatMessageResponse(
        @Schema(description = "Texto de resposta gerado pelo assistente", example = "Transação de R$ 55,00 registrada com sucesso na categoria Alimentação.")
        String response,

        @Schema(description = "Identificador da sessão de conversa para continuidade do contexto", example = "sessao-usuario-123")
        String conversationId
) {}
