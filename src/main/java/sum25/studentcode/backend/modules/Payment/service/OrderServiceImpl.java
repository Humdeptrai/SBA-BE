package sum25.studentcode.backend.modules.Payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.model.Transaction;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Payment.repository.OrderRepository;
import sum25.studentcode.backend.modules.Packs.repository.PackRepository;
import sum25.studentcode.backend.modules.Wallet.service.WalletService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PackRepository packRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    @Override
    public Order getOrderByPaymentReference(String paymentReference) {
        return orderRepository.findByPaymentReference(paymentReference);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order createPendingOrder(Long userId, Long packId) { // Đã đổi vị trí tham số cho nhất quán
        log.info("Creating PENDING Order for User {} and Pack {}", userId, packId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pack pack = packRepository.findById(packId)
                .orElseThrow(() -> new RuntimeException("Pack not found"));

        // Tính tổng giá trị nhận được từ gói (Credit)
        // Dựa trên định nghĩa Pack: packValue = Giá tiền = Số Credit.
        BigDecimal creditValue = pack.getPackValue();

        // Cần thêm Currency cho tiền thật (USD) để PayPal Service sử dụng
        String currency = "USD"; // Giả định tiền tệ là USD cho PayPal

        Order order = Order.builder()
                .user(user)
                .orderType(Order.OrderType.PACK_PURCHASE)
                .transactionValue(creditValue)
                .currency(currency) // Cần thiết cho Order để lưu loại tiền tệ
                .relatedEntityId(pack.getPackId())
                .relatedEntityType("PACK")
                .status(Order.OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    /**
     * CHỈ CẬP NHẬT TRẠNG THÁI ORDER. Logic cộng tiền và tạo Transaction đã chuyển ra ngoài
     * hoặc được thực hiện trong một hàm riêng biệt và được gọi từ Service bên ngoài (PayPalService).
     * @param externalRefId Mã giao dịch từ cổng thanh toán (Sale ID của PayPal hoặc TransId của Momo)
     */
    @Transactional
    @Override
    public void completeOrderSuccess(Order order, String externalRefId) {
        if (order.getStatus() != Order.OrderStatus.PENDING) return;

        // KHÔNG GỌI walletService.depositCredit() Ở ĐÂY NỮA

        // Cập nhật Order cuối cùng
        order.setStatus(Order.OrderStatus.COMPLETED); // Đã sửa từ PAID thành COMPLETED (cho luồng an toàn)
        order.setPaymentReference(externalRefId);
        // order.setTransaction(transaction); // KHÔNG SET Transaction ở đây, Transaction được tạo ở depositCredit

        orderRepository.save(order);

        log.info("✅ Order {} marked as COMPLETED successfully. PaymentRef: {}", order.getOrderId(), externalRefId);
    }

    /**
     * Phương thức chịu trách nhiệm cộng tiền an toàn, được gọi từ Webhook.
     * @return Bản ghi Transaction đã được tạo
     */
    @Transactional
    @Override
    public Transaction processPaymentSuccess(Order order, String externalRefId) {
        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            log.warn("Order {} is already completed. Skipping credit deposit.", order.getOrderId());
            // Trả về Transaction hiện tại nếu đã hoàn tất
            return order.getTransaction();
        }

        // 1. Ghi sổ cái (Tạo Transaction và Cập nhật Wallet)
        Transaction transaction = walletService.depositCredit(
                order.getUser(),
                order.getTransactionValue(), // Sử dụng giá trị Credit đã lưu trong Order
                order,
                externalRefId
        );

        // 2. Cập nhật Order status thành COMPLETED và lưu liên kết Transaction
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setPaymentReference(externalRefId);
        order.setTransaction(transaction); // Liên kết Transaction vào Order
        orderRepository.save(order);

        return transaction;
    }


    /**
     * 3. Đánh dấu Order thất bại.
     */
    @Transactional
    @Override
    public void failOrder(Order order) {
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(order);
            log.info("❌ Order {} marked as FAILED.", order.getOrderId());
        }
    }
}
