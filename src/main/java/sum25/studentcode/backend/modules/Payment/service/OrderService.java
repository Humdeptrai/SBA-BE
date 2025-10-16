package sum25.studentcode.backend.modules.Payment.service;

import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.Transaction;

public interface OrderService {
    Order createPendingOrder(Long packId, Long userId);
    void completeOrderSuccess(Order order, String externalReferenceId);
    void failOrder(Order order);
    Transaction processPaymentSuccess(Order order, String externalRefId);
    Order saveOrder(Order order);
    Order getOrderByPaymentReference(String paymentReference);

}
