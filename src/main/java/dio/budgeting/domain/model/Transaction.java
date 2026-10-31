package dio.budgeting.domain.model;

import dio.budgeting.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Transaction {

    private final TransactionId id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Category category;
    private final Instant createdAt;

    public Transaction(TransactionId id,
                       String description,
                       BigDecimal amount,
                       TransactionType type,
                       Category category,
                       Instant createdAt) {
        validate(id, description, amount, type, category, createdAt);
        this.id = id;
        this.description = description.trim();
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static Transaction create(String description,
                                     BigDecimal amount,
                                     TransactionType type,
                                     Category category) {
        return new Transaction(
                TransactionId.generate(),
                description,
                amount,
                type,
                category,
                Instant.now()
        );
    }

    public static Transaction reconstitute(TransactionId id,
                                           String description,
                                           BigDecimal amount,
                                           TransactionType type,
                                           Category category,
                                           Instant createdAt) {
        return new Transaction(id, description, amount, type, category, createdAt);
    }

    private void validate(TransactionId id,
                          String description,
                          BigDecimal amount,
                          TransactionType type,
                          Category category,
                          Instant createdAt) {
        if (id == null) {
            throw new DomainException("O identificador da transação não pode ser nulo.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new DomainException("A descrição da transação é obrigatória.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("O valor da transação deve ser positivo e maior que zero.");
        }
        if (type == null) {
            throw new DomainException("O tipo de transação (INCOME ou EXPENSE) é obrigatório.");
        }
        if (category == null) {
            throw new DomainException("A categoria da transação é obrigatória.");
        }
        if (!category.matchesType(type)) {
            throw new DomainException(String.format(
                    "A categoria %s não é compatível com o tipo de transação %s.",
                    category.name(), type.name()
            ));
        }
        if (createdAt == null) {
            throw new DomainException("A data de registro da transação não pode ser nula.");
        }
    }

    public TransactionId getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", category=" + category +
                ", createdAt=" + createdAt +
                '}';
    }
}
