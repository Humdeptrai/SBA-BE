package sum25.studentcode.backend.modules.Transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}