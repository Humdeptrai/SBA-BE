package sum25.studentcode.backend.modules.Wallet.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRequest {
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
}