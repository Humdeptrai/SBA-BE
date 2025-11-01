package sum25.studentcode.backend.modules.Transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Idempotency check - prevent duplicate transactions from same external payment
    boolean existsByExternalReferenceId(String externalReferenceId);

    Optional<Transaction> findByExternalReferenceId(String externalReferenceId);

    // Find transactions by user ID
    List<Transaction> findByUser_UserId(Long userId);
}