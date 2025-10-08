package sum25.studentcode.backend.modules.Transaction.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {
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
}