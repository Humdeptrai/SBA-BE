package sum25.studentcode.backend.modules.Payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PackPurchaseRequest {
    private Long packId; // ID của gói Credit người dùng chọn
    private String redirectUrl; // URL chuyển hướng sau khi thanh toán
    private String ipnUrl;      // URL nhận thông báo kết quả (IPN)
}
