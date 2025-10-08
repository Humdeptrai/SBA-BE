package sum25.studentcode.backend.modules.Transaction.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long transactionId;
    private Long walletId;
    private Long userId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceId;
    private String status;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}