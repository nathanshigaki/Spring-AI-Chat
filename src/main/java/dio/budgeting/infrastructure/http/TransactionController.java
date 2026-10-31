package dio.budgeting.infrastructure.http;

import dio.budgeting.application.dto.FinancialSummaryOutput;
import dio.budgeting.application.dto.TransactionOutput;
import dio.budgeting.application.usecase.DeleteTransactionUseCase;
import dio.budgeting.application.usecase.GetFinancialSummaryUseCase;
import dio.budgeting.application.usecase.ListTransactionsUseCase;
import dio.budgeting.application.usecase.RegisterTransactionUseCase;
import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.infrastructure.http.dto.TransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transações", description = "Endpoints REST para gestão manual de transações financeiras")
public class TransactionController {

    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetFinancialSummaryUseCase getFinancialSummaryUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public TransactionController(
            RegisterTransactionUseCase registerTransactionUseCase,
            ListTransactionsUseCase listTransactionsUseCase,
            GetFinancialSummaryUseCase getFinancialSummaryUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase
    ) {
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getFinancialSummaryUseCase = getFinancialSummaryUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nova transação", description = "Cadastra uma transação de receita ou despesa no sistema.")
    public TransactionOutput create(@RequestBody @Valid TransactionRequest request) {
        return registerTransactionUseCase.execute(request.toInput());
    }

    @GetMapping
    @Operation(summary = "Listar transações", description = "Lista todas as transações cadastradas, com suporte a filtros opcionais.")
    public List<TransactionOutput> list(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) TransactionType type
    ) {
        return listTransactionsUseCase.execute(category, type);
    }

    @GetMapping("/summary")
    @Operation(summary = "Balanço financeiro consolidado", description = "Retorna totais de receitas, despesas, saldo líquido e distribuição por categoria.")
    public FinancialSummaryOutput summary() {
        return getFinancialSummaryUseCase.execute();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remover/Estornar transação", description = "Exclui uma transação existente informando seu ID (UUID).")
    public String delete(@PathVariable String id) {
        return deleteTransactionUseCase.execute(id);
    }
}
