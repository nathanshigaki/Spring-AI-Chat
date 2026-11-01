package dio.budgeting.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do comando de voz direto")
public record VoiceMessageResponse(
        @Schema(description = "Texto transcrito a partir da voz do usuário", example = "Recebi 4500 reais de salário hoje")
        String transcription,

        @Schema(description = "Texto de resposta do assistente", example = "Receita de R$ 4.500,00 registrada com sucesso na categoria Salário.")
        String response,

        @Schema(description = "Identificador da sessão de conversa", example = "sessao-123")
        String conversationId
) {}
