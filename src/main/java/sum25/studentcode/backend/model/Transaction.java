package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "transactionId")
@ToString(exclude = {"wallet", "user", "order"}) // Đã thêm 'order'
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    // QUAN HỆ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    // Liên kết 1-1 với Order để biết giao dịch này đến từ đơn hàng nào
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    // PHÂN LOẠI

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // VD: DEPOSIT, USAGE, REFUND

    // GIÁ TRỊ VÀ SỐ DƯ (Giữ BigDecimal cho tính chính xác)

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount; // Số tiền/Credit thay đổi

    @Column(name = "balance_before", precision = 19, scale = 4, nullable = false)
    private BigDecimal balanceBefore; // Số dư trước giao dịch

    @Column(name = "balance_after", precision = 19, scale = 4, nullable = false)
    private BigDecimal balanceAfter; // Số dư sau giao dịch

    // MÔ TẢ & TRẠNG THÁI

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status; // VD: SUCCESS, FAILED

    @Column(name = "external_reference_id")
    private String externalReferenceId; // Mã giao dịch cổng thanh toán (nếu có)

    // QUẢN LÝ THỜI GIAN

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Trường transactionDate bị loại bỏ vì thừa với createdAt
    // Trường referenceId đã được thay thế bằng quan hệ @OneToOne với Order

    // Trong một file riêng: TransactionType.java
    public enum TransactionType {
        DEPOSIT,    // Nạp Credit (từ PACK_PURCHASE Order)
        USAGE,      // Trừ Credit (từ SERVICE_USAGE Order)
        REFUND      // Hoàn trả Credit
    }

    // Trong một file riêng: TransactionStatus.java
    public enum TransactionStatus {
        SUCCESS,    // Giao dịch hoàn thành
        FAILED      // Giao dịch thất bại (ví dụ: không đủ Credit)
    }
}