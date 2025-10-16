package sum25.studentcode.backend.modules.Transaction.dto.request;

import lombok.*;
import sum25.studentcode.backend.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private Long walletId;
    private Long userId;
    private Long orderId; // có thể null nếu không liên kết đơn hàng
    private Transaction.TransactionType transactionType; // DEPOSIT, USAGE, REFUND
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private Transaction.TransactionStatus status; // SUCCESS, FAILED
    private String externalReferenceId; // ID giao dịch PayPal/MoMo/Stripe...
}