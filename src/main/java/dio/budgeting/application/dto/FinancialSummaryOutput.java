package dio.budgeting.application.dto;

import java.math.BigDecimal;
import java.util.Map;

public record FinancialSummaryOutput(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance,
        int totalTransactions,
        Map<String, BigDecimal> expensesByCategory,
        Map<String, BigDecimal> incomesByCategory
) {}
