package dio.budgeting.application.usecase;

import dio.budgeting.application.dto.TransactionOutput;
import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.domain.repository.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "list-transactions", description = "Lista as transações financeiras cadastradas. Pode filtrar opcionalmente por categoria ou tipo (INCOME ou EXPENSE).")
    public List<TransactionOutput> execute(
            @ToolParam(description = "Categoria para filtrar as transações (opcional). Deixe nulo se quiser todas.", required = false)
            Category category,
            @ToolParam(description = "Tipo para filtrar as transações: INCOME ou EXPENSE (opcional). Deixe nulo se quiser ambos.", required = false)
            TransactionType type
    ) {
        List<Transaction> transactions;

        if (category != null) {
            transactions = transactionRepository.findAllByCategory(category);
        } else if (type != null) {
            transactions = transactionRepository.findAllByType(type);
        } else {
            transactions = transactionRepository.findAll();
        }

        return transactions.stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
