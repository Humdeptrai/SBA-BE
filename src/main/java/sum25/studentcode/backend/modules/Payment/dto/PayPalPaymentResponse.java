package sum25.studentcode.backend.modules.Payment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPaymentResponse {
    private String paymentId;
    private String approvalUrl;
    private String amount;
    private String currency;
    private String status;
}

