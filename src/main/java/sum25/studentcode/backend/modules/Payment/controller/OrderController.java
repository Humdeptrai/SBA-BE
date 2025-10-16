package sum25.studentcode.backend.modules.Payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.modules.Auth.dto.response.UserDetailsImpl;
import sum25.studentcode.backend.modules.Packs.service.PackService;
import sum25.studentcode.backend.modules.Payment.dto.PackPurchaseRequest;
import sum25.studentcode.backend.modules.Payment.dto.PayPalPaymentResponse;
import sum25.studentcode.backend.modules.Payment.service.PayPalService;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final PayPalService payPalService;
    private final PackService packService;

    /**
     * POST /api/orders/purchase: Khởi tạo yêu cầu mua gói Credit qua PayPal.
     * Service sẽ tạo Order, gọi API PayPal, và trả về approvalUrl cho client redirect.
     *
     * @param request PackPurchaseRequest
     * @param userDetails Thông tin User đang đăng nhập
     * @return PayPalPaymentResponse chứa approvalUrl để client redirect
     */
    @PostMapping("/orders/purchase")
    public ResponseEntity<PayPalPaymentResponse> initiatePackPurchase(
            @RequestBody PackPurchaseRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // Lấy ID người dùng hiện tại
        Long userId = userDetails.getUserId();

        try {
            // Gọi Service để tạo yêu cầu thanh toán với PayPal
            PayPalPaymentResponse response = payPalService.createPaymentRequest(request, userId);

            // Kiểm tra xem PayPal có trả về URL để phê duyệt (ApprovalUrl) hay không
            if (response.getApprovalUrl() != null && response.getStatus().equalsIgnoreCase("CREATED")) {
                // Nếu PayPal tạo thành công, trả về response cho FE
                return ResponseEntity.ok(response);
            } else {
                // Lỗi từ phía PayPal hoặc Service không thể tạo yêu cầu
                String errorMessage = "PayPal API error: Failed to get Approval URL. Status: " + response.getStatus();
                log.warn("Failed PayPal initiation for User {}: {}", userId, errorMessage);

                // Trả về lỗi 400 Bad Request
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            // Lỗi chung (ví dụ: lỗi kết nối mạng, lỗi mã hóa, lỗi logic nội bộ)
            log.error("Internal server error during PayPal initiation for User {}: {}", userId, e.getMessage(), e);

            // Trả về lỗi 500 với một thông báo lỗi chung
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PayPalPaymentResponse.builder()
                            .status("FAILED")
                            .build());
        }
    }

}
