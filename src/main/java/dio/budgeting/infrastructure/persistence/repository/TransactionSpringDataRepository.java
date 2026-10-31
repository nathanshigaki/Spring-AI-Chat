package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.model.Category;
import dio.budgeting.domain.model.TransactionType;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionSpringDataRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findAllByCategory(Category category);

    List<TransactionEntity> findAllByType(TransactionType type);
}
