package dio.budgeting.application.usecase;

import dio.budgeting.application.dto.FinancialSummaryOutput;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.domain.repository.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetFinancialSummaryUseCase {

    private final TransactionRepository transactionRepository;

    public GetFinancialSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-financial-summary", description = "Calcula e retorna o balanço financeiro consolidado: total de receitas, total de despesas, saldo líquido atual e detalhamento de gastos e receitas por categoria.")
    public FinancialSummaryOutput execute() {
        List<Transaction> transactions = transactionRepository.findAll();

        BigDecimal totalIncome = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getDisplayName(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        Map<String, BigDecimal> incomesByCategory = transactions.stream()
                .filter(tx -> tx.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getDisplayName(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        return new FinancialSummaryOutput(
                totalIncome,
                totalExpense,
                netBalance,
                transactions.size(),
                expensesByCategory,
                incomesByCategory
        );
    }
}
