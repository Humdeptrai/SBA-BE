package sum25.studentcode.backend.model;

import sum25.studentcode.backend.model.Order.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "logId")
@ToString(exclude = {"order"})
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    // QUAN HỆ

    // Liên kết 1-1 với đơn hàng mua Credit đang được xử lý
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private Order order;

    // THÔNG TIN TỪ MOMO/CỔNG THANH TOÁN

    @Column(name = "payment_gateway", nullable = false, length = 50)
    private String paymentGateway = "MOMO"; // Ví dụ: MOMO, VNPay, PayPal

    @Column(name = "gateway_order_id", unique = true)
    private String gatewayOrderId; // Mã giao dịch do MoMo tạo ra (partnerCode + orderId)

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount; // Số tiền đã gửi cho MoMo

    @Column(name = "response_code")
    private Integer responseCode; // Mã phản hồi cuối cùng từ MoMo (0 = Thành công)

    // LOG THANH TOÁN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // Trạng thái của quá trình thanh toán (PENDING, PAID, FAILED)

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody; // Dữ liệu JSON gửi đến MoMo

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody; // Dữ liệu JSON nhận được từ MoMo/IPN

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}