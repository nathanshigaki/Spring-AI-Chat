package dio.budgeting.domain.repository;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    List<Transaction> findAll();

    List<Transaction> findAllByCategory(Category category);

    List<Transaction> findAllByType(TransactionType type);

    void deleteById(TransactionId id);

    boolean existsById(TransactionId id);
}
