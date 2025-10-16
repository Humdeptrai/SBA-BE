package sum25.studentcode.backend.modules.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.PaymentLog;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Integer> {
    PaymentLog findByOrder(Order order);
}
