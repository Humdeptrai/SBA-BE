package sum25.studentcode.backend.modules.Payment.controller;

import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Payment.service.PayPalService;
import java.net.URI;
import java.util.Map;

/**
 * Controller này chỉ chuyên xử lý các bước tiếp theo sau khi đã tạo payment (redirect và webhook).
 * Logic tạo Payment (B1) đã được chuyển về OrderController.
 * Mọi logic nghiệp vụ (DB, PayPal API) đều nằm trong PayPalService.
 */
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PayPalService payPalService;

    // Sử dụng URL Redirect của Frontend (FE)
    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    /**
     * B2. Khi PayPal redirect về sau khi thanh toán thành công.
     * UPDATED: Now also creates transaction as fallback if webhook isn't called
     * @param paymentId ID thanh toán từ PayPal
     * @param PayerID ID người trả tiền từ PayPal
     */
    @GetMapping("/success")
    public ResponseEntity<Void> handleSuccessRedirect(
            @RequestParam String paymentId,
            @RequestParam String PayerID) {

        log.info("🔄 Handling PayPal success redirect. PaymentID: {}, PayerID: {}", paymentId, PayerID);

        String redirectUrl;

        try {
            // 1. Execute payment and get OrderId
            Long orderId = payPalService.executePaymentAndVerify(paymentId, PayerID);
            log.info("✅ PayPal payment executed successfully. OrderID: {}", orderId);

            // 2. FALLBACK: Create transaction if webhook hasn't been called yet
            // This ensures transaction is created even if webhook fails/delayed
            boolean transactionCreated = payPalService.createTransactionIfNotExists(paymentId, PayerID);

            if (transactionCreated) {
                log.info("💰 Transaction created via success redirect fallback for order: {}", orderId);
            } else {
                log.info("ℹ️ Transaction already exists (likely created by webhook) for order: {}", orderId);
            }

            // 3. Redirect to frontend with success status
            redirectUrl = frontendBaseUrl + "/payment-status?status=success&orderId=" + orderId;

        } catch (PayPalRESTException e) {
            log.error("❌ PayPal execution failed for paymentId {}: {}", paymentId, e.getMessage());
            redirectUrl = frontendBaseUrl + "/payment-status?status=failed&message=execution_failed";

        } catch (Exception e) {
            log.error("❌ Internal error during success handling for paymentId {}: {}", paymentId, e.getMessage());
            redirectUrl = frontendBaseUrl + "/payment-status?status=error&message=internal_error";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * B3. Nếu người dùng hủy thanh toán.
     */
    @GetMapping("/cancel")
    public ResponseEntity<Void> handleCancelRedirect() {
        log.warn("PayPal payment cancelled by user.");

        String redirectUrl = frontendBaseUrl + "/payment-status?status=cancelled";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * B4. Endpoint BẮT BUỘC nhận IPN (Instant Payment Notification) từ PayPal.
     * Đây là nơi an toàn nhất để ghi lại giao dịch và cộng Credit.
     * @param webhookEvent Dữ liệu sự kiện Webhook từ PayPal
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePayPalWebhook(@RequestBody String webhookEvent,
                                                      @RequestHeader Map<String, String> headers) {
        log.info("Received PayPal Webhook event. Starting verification and processing.");

        try {
            // GỌI SERVICE: Xử lý toàn bộ logic: Xác thực, phân tích, cập nhật DB (Wallet, Transaction)
            payPalService.processWebhook(webhookEvent, headers);

            // Trả về 200 OK để xác nhận đã nhận được Webhook thành công
            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            log.error("Error processing PayPal Webhook. Payload: {}", webhookEvent, e);
            // Trả về lỗi 500 để PayPal thử gửi lại thông báo sau
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    // Endpoint GET /api/paypal/create đã bị loại bỏ vì nó nên nằm trong OrderController và dùng POST.
}
