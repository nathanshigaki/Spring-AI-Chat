package dio.budgeting.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record TransactionId(UUID value) implements Serializable {

    public TransactionId {
        Objects.requireNonNull(value, "TransactionId não pode ser nulo");
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }

    public static TransactionId of(String value) {
        Objects.requireNonNull(value, "String para TransactionId não pode ser nula");
        return new TransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
