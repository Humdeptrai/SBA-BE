package sum25.studentcode.backend.modules.Payment.service;

import sum25.studentcode.backend.model.*;

import sum25.studentcode.backend.modules.Auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.modules.Payment.Utils.MoMoUtil;
import sum25.studentcode.backend.modules.Payment.dto.MomoCreateRequest;
import sum25.studentcode.backend.modules.Payment.dto.MomoIPNRequest;
import sum25.studentcode.backend.modules.Payment.dto.MomoPaymentResponse;
import sum25.studentcode.backend.modules.Payment.dto.PackPurchaseRequest;
import sum25.studentcode.backend.modules.Payment.repository.OrderRepository;
import sum25.studentcode.backend.modules.Payment.repository.PackRepository;
import sum25.studentcode.backend.modules.Payment.repository.PaymentLogRepository;
import sum25.studentcode.backend.modules.Payment.service.OrderService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {

    // Dependencies
    private final OrderService orderService;
    private final PaymentLogRepository paymentLogRepository;
    private final OrderRepository orderRepository;
    private final PackRepository packRepository;
    private final UserRepository userRepository;

    // Cấu hình từ application.properties
    @Value("${momo.secretKey}") private String secretKey;
    @Value("${momo.createUrl}") private String createUrl;
    // ... các config khác

    /**
     * 1. Khởi tạo yêu cầu thanh toán.
     */
    public MomoPaymentResponse createPaymentRequest(PackPurchaseRequest request, Long userId) {
        // 1. Tìm Pack và tạo Order PENDING
        Pack pack = packRepository.findById(request.getPackId())
                .orElseThrow(() -> new RuntimeException("Pack not found: " + request.getPackId()));

        Order order = orderService.createPendingOrder(userId, pack.getPackId()); // Tạo Order PENDING

        // 2. Chuẩn bị và tính Signature
        MomoCreateRequest momoRequest = buildMomoRequest(order, request); // Hàm tiện ích
        String signature = MoMoUtil.createSignature(momoRequest, secretKey); // Hàm tiện ích
        momoRequest.setSignature(signature);

        // 3. Gọi MoMo API (sử dụng RestTemplate/WebClient)
        MomoPaymentResponse momoApiResult;
        try {
            // Giả định: callExternalApi là hàm gọi MoMo và trả về MomoPaymentResponse
            momoApiResult = callExternalApi(momoRequest, createUrl);
        } catch (Exception e) {
            log.error("Error calling MoMo API: {}", e.getMessage());
            // Cần cập nhật Order sang trạng thái API_CALL_FAILED
            orderService.failOrder(order);
            throw new RuntimeException("Payment initiation failed.");
        }

        // 4. Ghi PaymentLog (Nhật ký giao tiếp)
        savePaymentLog(order, momoRequest, momoApiResult);

        // 5. Trả về cho Frontend
        return momoApiResult;
    }

    /**
     * 2. Xử lý thông báo kết quả từ MoMo (IPN).
     */
    public void handleIpnNotification(MomoIPNRequest ipnRequest) {
        log.info("Received IPN for Order: {}", ipnRequest.getOrderId());

        // 1. Xác thực chữ ký
        if (!MoMoUtil.validateSignature(ipnRequest, secretKey)) { // Hàm tiện ích
            log.error("IPN Signature validation FAILED for order: {}", ipnRequest.getOrderId());
            return; // Dừng xử lý
        }

        // 2. Tìm Order và PaymentLog
        int orderId = Integer.parseInt(ipnRequest.getOrderId());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        PaymentLog paymentLog = paymentLogRepository.findByOrder(order);

        // Tránh xử lý trùng lặp
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            log.warn("Order {} already processed. Status: {}", orderId, order.getStatus());
            return;
        }

        // 3. Xử lý trạng thái giao dịch
        paymentLog.setResponseCode(ipnRequest.getResultCode());
        paymentLog.setResponseBody(ipnRequest.toString()); // Lưu toàn bộ JSON IPN

        if (ipnRequest.getResultCode() == 0) {
            // Thanh toán THÀNH CÔNG
            orderService.completeOrderSuccess(order, ipnRequest.getTransId());
            paymentLog.setStatus(Order.OrderStatus.PAID);
        } else {
            // Thanh toán THẤT BẠI
            orderService.failOrder(order);
            paymentLog.setStatus(Order.OrderStatus.FAILED);
        }

        paymentLogRepository.save(paymentLog);
        log.info("✅ Order {} processing complete. New status: {}", orderId, order.getStatus());
    }

    // --- Hàm Tiện ích ---

    private void savePaymentLog(Order order, MomoCreateRequest request, MomoPaymentResponse response) {
        PaymentLog log = PaymentLog.builder()
                .order(order)
                .status(response.getResultCode() == 0 ? Order.OrderStatus.PENDING : Order.OrderStatus.FAILED)
                .requestBody(request.toString())
                .responseBody(response.toString())
                .gatewayOrderId(response.getOrderId() != null ? response.getOrderId().toString() : null)
                .build();
        paymentLogRepository.save(log);
    }

    // Giả định:
    // Trong MomoService.java

    private MomoCreateRequest buildMomoRequest(Order order, PackPurchaseRequest request) {
        // 1. Lấy cấu hình
        String partnerCode = "YOUR_PARTNER_CODE";
        String accessKey = "YOUR_ACCESS_KEY";
        String secretKey = "YOUR_SECRET_KEY";

        // 2. Định dạng số tiền (MoMo thường dùng Long/Int)
        Long amountLong = order.getTransactionValue().multiply(new BigDecimal(1)).longValue();

        MomoCreateRequest momoRequest = MomoCreateRequest.builder()
                .partnerCode(partnerCode)
                .accessKey(accessKey)
                .requestId(String.valueOf(order.getOrderId())) // Sử dụng Order ID làm Request ID
                .orderId(String.valueOf(order.getOrderId()))
                .amount(amountLong)
                .orderInfo("Nap Credit mua goi " + order.getRelatedEntityId())
                .redirectUrl(request.getRedirectUrl())
                .ipnUrl(request.getIpnUrl())
                .requestType("payWithMethod") // Hoặc các loại khác tùy theo MoMo
                .extraData("") // Thường là chuỗi rỗng
                .lang("vi")
                .build();

        // 3. TÍNH CHỮ KÝ BẢO MẬT
        String signature = MoMoUtil.createSignature(momoRequest, secretKey);
        momoRequest.setSignature(signature);

        return momoRequest;
    }
    // Trong MomoService.java

//    private final WebClient webClient;
//
//    private MomoPaymentResponse callExternalApi(MomoCreateRequest request, String url) {
//        try {
//            // 1. Gửi POST Request (thường dùng WebClient hoặc RestTemplate)
//            MomoPaymentResponse response = webClient.post()
//                    .uri(url)
//                    .bodyValue(request) // Gửi JSON body
//                    .retrieve()
//                    .bodyToMono(MomoPaymentResponse.class) // Chuyển JSON response thành DTO
//                    .block(); // Chặn đồng bộ (có thể dùng async trong môi trường khác)
//
//            return response;
//
//        } catch (Exception e) {
//            log.error("Error connecting to MoMo API: {}", e.getMessage());
//            // Trả về response lỗi nội bộ
//            return MomoPaymentResponse.builder()
//                    .resultCode(-1)
//                    .message("Internal service error or MoMo connection failure.")
//                    .build();
//        }
//    }
}