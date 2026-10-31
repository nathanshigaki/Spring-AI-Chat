package dio.budgeting.application.usecase;

import dio.budgeting.domain.exception.DomainException;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.repository.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class DeleteTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public DeleteTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "delete-transaction", description = "Remove ou estorna uma transação financeira existente através do seu identificador (UUID).")
    public String execute(
            @ToolParam(description = "ID único da transação em formato UUID a ser estornada/removida")
            String transactionId
    ) {
        TransactionId id;
        try {
            id = TransactionId.of(transactionId);
        } catch (Exception e) {
            throw new DomainException("ID de transação inválido: " + transactionId);
        }

        if (!transactionRepository.existsById(id)) {
            throw new DomainException("Transação não encontrada com o ID: " + transactionId);
        }

        transactionRepository.deleteById(id);
        return "Transação " + transactionId + " removida com sucesso.";
    }
}
