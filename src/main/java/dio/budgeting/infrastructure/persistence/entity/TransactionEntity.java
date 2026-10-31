package dio.budgeting.infrastructure.persistence.entity;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.model.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TransactionEntity() {
    }

    public TransactionEntity(String id,
                             String description,
                             BigDecimal amount,
                             TransactionType type,
                             Category category,
                             Instant createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static TransactionEntity fromDomain(Transaction tx) {
        return new TransactionEntity(
                tx.getId().toString(),
                tx.getDescription(),
                tx.getAmount(),
                tx.getType(),
                tx.getCategory(),
                tx.getCreatedAt()
        );
    }

    public Transaction toDomain() {
        return Transaction.reconstitute(
                TransactionId.of(this.id),
                this.description,
                this.amount,
                this.type,
                this.category,
                this.createdAt
        );
    }

    public String getId() {
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
}
