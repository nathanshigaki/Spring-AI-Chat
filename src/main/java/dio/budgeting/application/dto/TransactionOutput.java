package dio.budgeting.application.dto;

import dio.budgeting.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionOutput(
        String id,
        String description,
        BigDecimal amount,
        String type,
        String typeDescription,
        String category,
        String categoryDisplayName,
        Instant createdAt
) {
    public static TransactionOutput from(Transaction tx) {
        return new TransactionOutput(
                tx.getId().toString(),
                tx.getDescription(),
                tx.getAmount(),
                tx.getType().name(),
                tx.getType().getDescription(),
                tx.getCategory().name(),
                tx.getCategory().getDisplayName(),
                tx.getCreatedAt()
        );
    }
}
