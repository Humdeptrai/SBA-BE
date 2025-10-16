package sum25.studentcode.backend.modules.Transaction.dto.response;

import lombok.*;
import sum25.studentcode.backend.model.Transaction.TransactionStatus;
import sum25.studentcode.backend.model.Transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long transactionId;
    private Long walletId;
    private Long userId;
    private Long orderId; // có thể null
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private TransactionStatus status;
    private String externalReferenceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
