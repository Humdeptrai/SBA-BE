package sum25.studentcode.backend.modules.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByPaymentReference (String paymentReference);
}
