package dio.budgeting.domain;

import dio.budgeting.domain.exception.DomainException;
import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Deve criar uma transação de despesa válida com sucesso")
    void shouldCreateValidExpenseTransaction() {
        var tx = Transaction.create(
                "Supermercado Mensal",
                new BigDecimal("450.75"),
                TransactionType.EXPENSE,
                Category.ALIMENTACAO
        );

        assertNotNull(tx.getId());
        assertEquals("Supermercado Mensal", tx.getDescription());
        assertEquals(new BigDecimal("450.75"), tx.getAmount());
        assertEquals(TransactionType.EXPENSE, tx.getType());
        assertEquals(Category.ALIMENTACAO, tx.getCategory());
        assertNotNull(tx.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar uma transação de receita válida com sucesso")
    void shouldCreateValidIncomeTransaction() {
        var tx = Transaction.create(
                "Salário Empresa Tech",
                new BigDecimal("8500.00"),
                TransactionType.INCOME,
                Category.SALARIO
        );

        assertNotNull(tx.getId());
        assertEquals("Salário Empresa Tech", tx.getDescription());
        assertEquals(new BigDecimal("8500.00"), tx.getAmount());
        assertEquals(TransactionType.INCOME, tx.getType());
        assertEquals(Category.SALARIO, tx.getCategory());
        assertTrue(tx.getType().isIncome());
    }

    @Test
    @DisplayName("Deve falhar ao criar transação com valor zero ou negativo")
    void shouldFailWhenAmountIsZeroOrNegative() {
        assertThrows(DomainException.class, () ->
                Transaction.create("Mercado", BigDecimal.ZERO, TransactionType.EXPENSE, Category.ALIMENTACAO)
        );

        assertThrows(DomainException.class, () ->
                Transaction.create("Mercado", new BigDecimal("-10.00"), TransactionType.EXPENSE, Category.ALIMENTACAO)
        );
    }

    @Test
    @DisplayName("Deve falhar ao criar transação com descrição vazia ou nula")
    void shouldFailWhenDescriptionIsBlankOrNull() {
        assertThrows(DomainException.class, () ->
                Transaction.create("", new BigDecimal("100.00"), TransactionType.EXPENSE, Category.ALIMENTACAO)
        );

        assertThrows(DomainException.class, () ->
                Transaction.create("   ", new BigDecimal("100.00"), TransactionType.EXPENSE, Category.ALIMENTACAO)
        );

        assertThrows(DomainException.class, () ->
                Transaction.create(null, new BigDecimal("100.00"), TransactionType.EXPENSE, Category.ALIMENTACAO)
        );
    }

    @Test
    @DisplayName("Deve falhar quando categoria não for compatível com o tipo de transação")
    void shouldFailWhenCategoryDoesNotMatchTransactionType() {
        assertThrows(DomainException.class, () ->
                Transaction.create("Tentativa inválida", new BigDecimal("100.00"), TransactionType.EXPENSE, Category.SALARIO)
        );

        assertThrows(DomainException.class, () ->
                Transaction.create("Tentativa inválida", new BigDecimal("100.00"), TransactionType.INCOME, Category.ALIMENTACAO)
        );
    }

    @Test
    @DisplayName("Deve reconstituir transação existente corretamente")
    void shouldReconstituteTransaction() {
        var uuid = UUID.randomUUID();
        var id = TransactionId.of(uuid);
        var now = Instant.now();

        var tx = Transaction.reconstitute(
                id,
                "Farmácia",
                new BigDecimal("89.90"),
                TransactionType.EXPENSE,
                Category.SAUDE,
                now
        );

        assertEquals(id, tx.getId());
        assertEquals("Farmácia", tx.getDescription());
        assertEquals(now, tx.getCreatedAt());
    }
}
