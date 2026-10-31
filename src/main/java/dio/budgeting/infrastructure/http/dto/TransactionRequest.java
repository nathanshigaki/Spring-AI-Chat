package dio.budgeting.infrastructure.http.dto;

import dio.budgeting.application.dto.RegisterTransactionInput;
import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload para registro manual de transação financeira")
public record TransactionRequest(
        @NotBlank(message = "A descrição é obrigatória")
        @Schema(description = "Descrição da transação", example = "Supermercado Mensal")
        String description,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        @Schema(description = "Valor em reais", example = "350.75")
        BigDecimal amount,

        @NotNull(message = "O tipo de transação é obrigatório")
        @Schema(description = "Tipo: INCOME ou EXPENSE", example = "EXPENSE")
        TransactionType type,

        @NotNull(message = "A categoria é obrigatória")
        @Schema(description = "Categoria de receita ou despesa", example = "ALIMENTACAO")
        Category category
) {
    public RegisterTransactionInput toInput() {
        return new RegisterTransactionInput(description, amount, type, category);
    }
}
