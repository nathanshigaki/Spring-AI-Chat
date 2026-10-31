package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.Transaction;
import dio.budgeting.domain.model.TransactionId;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.domain.repository.TransactionRepository;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionSpringDataRepository repository;

    public JpaTransactionRepository(TransactionSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.fromDomain(transaction);
        var saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return repository.findById(id.toString())
                .map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return repository.findAll().stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return repository.findAllByCategory(category).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByType(TransactionType type) {
        return repository.findAllByType(type).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(TransactionId id) {
        repository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(TransactionId id) {
        return repository.existsById(id.toString());
    }
}
