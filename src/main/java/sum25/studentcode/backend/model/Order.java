package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "orderId")
@ToString(exclude = {"user", "transaction"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // Người dùng tạo đơn hàng

    // PHÂN LOẠI ĐƠN HÀNG

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType; // VD: PACK_PURCHASE, CREATE_AI_TEST, STUDENT_REGISTER

    // GIÁ TRỊ GIAO DỊCH

    @Column(name = "transaction_value", precision = 19, scale = 4, nullable = false)
    private BigDecimal transactionValue;

    // Loại tiền tệ thực tế (Chỉ dùng cho PACK_PURCHASE)
    @Column(length = 3)
    private String currency; // VD: VND, USD


    @Column
    private String description;

    // LIÊN KẾT NGHIỆP VỤ

    @Column(name = "related_entity_id")
    private Long relatedEntityId; // ID của đối tượng liên quan (PackId, TestId, MatrixId)

    @Column(name = "related_entity_type")
    private String relatedEntityType; // Loại đối tượng liên quan (VD: "PACK", "TEST"). Vẫn cần để xác định ID là của Entity nào.

    // THÔNG TIN THANH TOÁN VÀ TRẠNG THÁI

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // Trạng thái đơn hàng (VD: PENDING, PAID, COMPLETED, FAILED)

    @Column(name = "payment_reference")
    private String paymentReference; // Mã giao dịch cổng thanh toán (chỉ dùng cho PACK_PURCHASE)

    // Liên kết 1-1 với bản ghi Transaction (Ghi nhận sự thay đổi Credit trong ví)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    private Transaction transaction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Trong một file riêng: OrderType.java (Đã Gộp ServiceType)
    public enum OrderType {
        // Giao dịch Mua Credit (Tiền thật)
        PACK_PURCHASE,

        // Giao dịch Sử dụng Dịch vụ (Trừ Credit)
        CREATE_AI_TEST,
        STUDENT_REGISTER,
        USE_MATRIX,
        // ... Thêm các dịch vụ khác nếu có
    }

    // Trong một file riêng: OrderStatus.java (Giữ nguyên)
    public enum OrderStatus {
        PENDING,
        PAID,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELED
    }
}