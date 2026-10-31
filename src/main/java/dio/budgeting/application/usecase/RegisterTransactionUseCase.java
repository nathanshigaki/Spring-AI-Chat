package dio.budgeting.application.usecase;

import dio.budgeting.application.dto.RegisterTransactionInput;
import dio.budgeting.application.dto.TransactionOutput;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.repository.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class RegisterTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public RegisterTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "register-transaction", description = "Registra uma nova transação financeira (receita ou despesa) com valor, descrição, tipo e categoria no sistema.")
    public TransactionOutput execute(RegisterTransactionInput input) {
        var transaction = Transaction.create(
                input.description(),
                input.amount(),
                input.type(),
                input.category()
        );

        var saved = transactionRepository.save(transaction);
        return TransactionOutput.from(saved);
    }
}
