package sum25.studentcode.backend.modules.Wallet.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletResponse {
    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}