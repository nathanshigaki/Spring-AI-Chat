package dio.budgeting.application.dto;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.TransactionType;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;

public record RegisterTransactionInput(
        @ToolParam(description = "Descrição detalhada da transação, ex: 'Compras no supermercado', 'Salário mensal', 'Farmácia'")
        String description,

        @ToolParam(description = "Valor numérico da transação em reais, sempre positivo, ex: 150.50")
        BigDecimal amount,

        @ToolParam(description = "Tipo da transação: INCOME (para receita/ganho/salário) ou EXPENSE (para gasto/despesa/pagamento)")
        TransactionType type,

        @ToolParam(description = "Categoria da transação: SALARIO, INVESTIMENTOS, FREELANCE, OUTRAS_RECEITAS (para receitas); ALIMENTACAO, MORADIA, TRANSPORTE, SAUDE, EDUCACAO, LAZER, OUTRAS_DESPESAS (para despesas)")
        Category category
) {}
