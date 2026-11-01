package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionSpringDataRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findAllByCategory(Category category);

    List<TransactionEntity> findAllByType(TransactionType type);
}
