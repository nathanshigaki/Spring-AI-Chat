package dio.budgeting.application;

import dio.budgeting.application.dto.RegisterTransactionInput;
import dio.budgeting.application.usecase.DeleteTransactionUseCase;
import dio.budgeting.application.usecase.GetFinancialSummaryUseCase;
import dio.budgeting.application.usecase.ListTransactionsUseCase;
import dio.budgeting.application.usecase.RegisterTransactionUseCase;
import dio.budgeting.domain.exception.DomainException;
import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationUseCasesTest {

    @Mock
    private TransactionRepository transactionRepository;

    private RegisterTransactionUseCase registerTransactionUseCase;
    private GetFinancialSummaryUseCase getFinancialSummaryUseCase;
    private ListTransactionsUseCase listTransactionsUseCase;
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @BeforeEach
    void setUp() {
        registerTransactionUseCase = new RegisterTransactionUseCase(transactionRepository);
        getFinancialSummaryUseCase = new GetFinancialSummaryUseCase(transactionRepository);
        listTransactionsUseCase = new ListTransactionsUseCase(transactionRepository);
        deleteTransactionUseCase = new DeleteTransactionUseCase(transactionRepository);
    }

    @Test
    @DisplayName("RegisterTransactionUseCase: deve registrar receita com sucesso")
    void shouldRegisterIncomeSuccessfully() {
        var input = new RegisterTransactionInput(
                "Salário Mensal",
                new BigDecimal("6000.00"),
                TransactionType.INCOME,
                Category.SALARIO
        );

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var output = registerTransactionUseCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals("Salário Mensal", output.description());
        assertEquals(new BigDecimal("6000.00"), output.amount());
        assertEquals("INCOME", output.type());
        assertEquals("SALARIO", output.category());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("GetFinancialSummaryUseCase: deve calcular balanço, receitas, despesas e saldo líquido")
    void shouldCalculateFinancialSummaryCorrectly() {
        var income = Transaction.create("Salário", new BigDecimal("5000.00"), TransactionType.INCOME, Category.SALARIO);
        var expense1 = Transaction.create("Aluguel", new BigDecimal("1500.00"), TransactionType.EXPENSE, Category.MORADIA);
        var expense2 = Transaction.create("Mercado", new BigDecimal("500.00"), TransactionType.EXPENSE, Category.ALIMENTACAO);

        when(transactionRepository.findAll()).thenReturn(List.of(income, expense1, expense2));

        var summary = getFinancialSummaryUseCase.execute();

        assertEquals(new BigDecimal("5000.00"), summary.totalIncome());
        assertEquals(new BigDecimal("2000.00"), summary.totalExpense());
        assertEquals(new BigDecimal("3000.00"), summary.netBalance());
        assertEquals(3, summary.totalTransactions());
        assertEquals(new BigDecimal("1500.00"), summary.expensesByCategory().get("Moradia"));
        assertEquals(new BigDecimal("500.00"), summary.expensesByCategory().get("Alimentação"));
        assertEquals(new BigDecimal("5000.00"), summary.incomesByCategory().get("Salário"));
    }

    @Test
    @DisplayName("ListTransactionsUseCase: deve filtrar por categoria quando informado")
    void shouldListTransactionsByCategory() {
        var tx = Transaction.create("Uber", new BigDecimal("35.00"), TransactionType.EXPENSE, Category.TRANSPORTE);
        when(transactionRepository.findAllByCategory(Category.TRANSPORTE)).thenReturn(List.of(tx));

        var result = listTransactionsUseCase.execute(Category.TRANSPORTE, null);

        assertEquals(1, result.size());
        assertEquals("TRANSPORTE", result.get(0).category());
        verify(transactionRepository, times(1)).findAllByCategory(Category.TRANSPORTE);
    }

    @Test
    @DisplayName("DeleteTransactionUseCase: deve remover transação existente")
    void shouldDeleteExistingTransaction() {
        var id = TransactionId.generate();
        when(transactionRepository.existsById(id)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(id);

        var result = deleteTransactionUseCase.execute(id.toString());

        assertTrue(result.contains("removida com sucesso"));
        verify(transactionRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DeleteTransactionUseCase: deve lançar exceção se transação não existir")
    void shouldThrowExceptionWhenDeletingNonExistentTransaction() {
        var id = TransactionId.generate();
        when(transactionRepository.existsById(id)).thenReturn(false);

        assertThrows(DomainException.class, () -> deleteTransactionUseCase.execute(id.toString()));
        verify(transactionRepository, never()).deleteById(any());
    }
}
