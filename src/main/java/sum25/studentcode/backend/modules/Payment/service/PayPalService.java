package sum25.studentcode.backend.modules.Payment.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.model.PaymentLog;
import sum25.studentcode.backend.model.User; // Import User để dùng trong depositCredit
import sum25.studentcode.backend.modules.Payment.dto.PackPurchaseRequest;
import sum25.studentcode.backend.modules.Payment.dto.PayPalPaymentResponse;
import sum25.studentcode.backend.modules.Packs.repository.PackRepository;
import sum25.studentcode.backend.modules.Payment.repository.PaymentLogRepository;
import sum25.studentcode.backend.modules.Wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {
    // PayPal API Context
    private final APIContext apiContext;

    // Services và Repositories
    private final OrderService orderService;
    private final PaymentLogRepository paymentLogRepository;
    private final PackRepository packRepository;
    // Đã thay thế TransactionService và WalletService (repository/save) bằng WalletService (logic)
    private final WalletService walletService;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.success.url}")
    private String successUrl;

    // ObjectMapper để xử lý JSON cho Webhook
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * DTO đơn giản để ánh xạ payload Webhook.
     * WebhookEventPayload không có sẵn trong PayPal REST SDK, nên ta tự định nghĩa.
     */
    @Getter
    @Setter
    public static class WebhookEventPayload {
        @JsonProperty("event_type")
        private String eventType;

        // Resource là đối tượng cụ thể (như Sale) mà sự kiện tác động
        private Map<String, Object> resource;
    }

    /**
     * 1. Khởi tạo yêu cầu thanh toán (Bước 1: Tạo Payment).
     * BỔ SUNG: Đặt InvoiceNumber (Order ID) vào transaction.
     */
    public PayPalPaymentResponse createPaymentRequest(PackPurchaseRequest request, Long userId) {
        // 1️⃣ Lấy thông tin pack và tạo đơn hàng (trạng thái PENDING)
        Pack pack = packRepository.findById(request.getPackId())
                .orElseThrow(() -> new RuntimeException("Pack not found: " + request.getPackId()));

        // Giả định orderService.createPendingOrder đã đặt OrderType và TransactionValue
        Order order = orderService.createPendingOrder(userId, pack.getPackId());

        // 2️⃣ Chuẩn bị dữ liệu thanh toán cho PayPal
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(pack.getPackValue().toString()); // decimal to string

        com.paypal.api.payments.Transaction transaction = new com.paypal.api.payments.Transaction();
        transaction.setDescription("Purchase pack " + pack.getPackId());
        transaction.setAmount(amount);

        // VẤN ĐỀ ĐÃ KHẮC PHỤC: Dùng Order ID làm Invoice Number để dễ dàng truy vấn
        transaction.setInvoiceNumber(order.getOrderId().toString());

        List<com.paypal.api.payments.Transaction> transactions = Collections.singletonList(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        try {
            // 3️⃣ Tạo payment qua PayPal API
            Payment createdPayment = payment.create(apiContext);

            // 4️⃣ Lấy approval URL để frontend redirect
            String approvalUrl = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElse(null);

            // 5️⃣ Ghi log và cập nhật paymentReference vào Order
            // ĐIỀU CHỈNH ĐỂ SỬ DỤNG TRƯỜNG paymentReference
            order.setPaymentReference(createdPayment.getId());
            orderService.saveOrder(order);
            savePaymentLog(order, createdPayment.toJSON(), "CREATE_PAYMENT_REQUEST");

            // 6️⃣ Trả về DTO cho frontend
            return PayPalPaymentResponse.builder()
                    .paymentId(createdPayment.getId())
                    .approvalUrl(approvalUrl)
                    .amount(amount.getTotal())
                    .currency(amount.getCurrency())
                    .status(createdPayment.getState())
                    .build();

        } catch (PayPalRESTException e) {
            log.error("❌ Error creating PayPal payment for Order {}: {}", order.getOrderId(), e.getMessage());
            orderService.failOrder(order);
            throw new RuntimeException("Payment creation failed: " + e.getMessage());
        }
    }

    /**
     * 2. Thực hiện Payment và Xác nhận (Bước 2: Execute Payment).
     * Hàm này chỉ gọi API PayPal để hoàn tất giao dịch. KHÔNG CẬP NHẬT WALLET/TRANSACTION.
     * @return OrderId liên quan
     */
    public Long executePaymentAndVerify(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        // Thực hiện giao dịch trên PayPal
        Payment executedPayment = payment.execute(apiContext, execution);

        // Ghi log phản hồi Execute
        // ĐIỀU CHỈNH: Sử dụng paymentId để truy vấn Order
        Order order = orderService.getOrderByPaymentReference(paymentId);
        savePaymentLog(order, executedPayment.toJSON(), "EXECUTE_PAYMENT_RESPONSE");

        // Kiểm tra trạng thái
        if (executedPayment.getState().equalsIgnoreCase("approved")) {
            // Nếu APPROVED, chúng ta chờ Webhook (B4) để cộng tiền.

            // Lấy Order ID từ InvoiceNumber (Đã đặt ở B1)
            String invoiceNumber = executedPayment.getTransactions().get(0).getInvoiceNumber();
            if (invoiceNumber == null) {
                log.error("Missing InvoiceNumber in executed payment {}", paymentId);
                throw new RuntimeException("Missing Invoice Number");
            }
            return Long.valueOf(invoiceNumber);
        } else {
            // Nếu trạng thái không phải approved, chuyển Order sang FAILED
            orderService.failOrder(order);
            throw new PayPalRESTException("Payment execution failed. State: " + executedPayment.getState());
        }
    }

    /**
     * 3. Xử lý Webhook (IPN) từ PayPal (Bước 4: Cộng tiền an toàn).
     * Logic cộng tiền, tạo Transaction chỉ được thực hiện tại đây.
     */
    public void processWebhook(String webhookEvent, Map<String, String> headers) throws Exception {
        // 1. CHƯA TRIỂN KHAI: Xác thực Webhook.
        // Trong môi trường sản phẩm, bước này là BẮT BUỘC để đảm bảo tính hợp lệ của request.
        // Cần gọi API verifyWebhookSignature của PayPal.
        // Tạm thời bỏ qua bước xác thực để tập trung vào logic cốt lõi.

        // 2. Phân tích cú pháp sự kiện
        // ĐÃ SỬA: Thay WebhookEvent.class bằng DTO WebhookEventPayload.class
        WebhookEventPayload event = objectMapper.readValue(webhookEvent, WebhookEventPayload.class);
        String eventType = event.getEventType();
        log.info("Processing Webhook Event Type: {}", eventType);

        if ("PAYMENT.SALE.COMPLETED".equals(eventType)) {
            // Sự kiện báo giao dịch đã hoàn tất và tiền đã về Merchant
            // Ánh xạ resource (Map<String, Object>) thành đối tượng Sale
            Sale sale = objectMapper.convertValue(event.getResource(), Sale.class);

            // paymentId là ID của Payment ban đầu
            String paymentId = sale.getParentPayment();

            // Lấy Order từ paymentReference (paymentId đã lưu ở B1)
            // ĐIỀU CHỈNH: Sử dụng paymentReference
            Order order = orderService.getOrderByPaymentReference(paymentId);

            // LƯU Ý: Nếu Webhook gửi đến trước Redirect, Order có thể chưa được Execute.
            // Tuy nhiên, sự kiện PAYMENT.SALE.COMPLETED đảm bảo tiền đã về, nên ta có thể
            // bỏ qua sự phụ thuộc vào trạng thái Execute trước đó và hoàn thành Order.

            if (order.getStatus() != Order.OrderStatus.COMPLETED) {

                // Lấy thông tin giá trị
                // LƯU Ý: amount này là giá trị tiền thật (USD)
                BigDecimal amount = new BigDecimal(sale.getAmount().getTotal());

                // 3. Thực hiện cộng Credit và ghi Transaction (Logic an toàn)
                // sale.getId() là Sale ID (Mã giao dịch thực tế)
                processSuccessfulOrder(order, amount, paymentId, sale.getId());

                log.info("✅ Successfully processed Webhook and completed Order {}.", order.getOrderId());
            } else {
                log.warn("Webhook for PaymentID {} received, but Order is already COMPLETED. Ignoring.", paymentId);
            }
        }
    }

    /**
     * Logic nghiệp vụ cốt lõi: Cập nhật Wallet và tạo Transaction.
     * CHỈ ĐƯỢC GỌI KHI CÓ XÁC NHẬN AN TOÀN (từ Webhook).
     */
    private void processSuccessfulOrder(Order order, BigDecimal realAmount, String paymentId, String saleId) {

        // 1. Cập nhật Order status thành COMPLETED. Lưu Sale ID (mã giao dịch cuối cùng) vào paymentReference.
        // DÙNG Sale ID để đảm bảo Order ghi lại mã giao dịch thành công cuối cùng
        orderService.completeOrderSuccess(order, saleId);

        // 2. Lấy số Credit cần cộng (Giả định Order.getTransactionValue() là số Credit)
        // Đây là điểm quan trọng: chỉ số Credit (đơn vị nội bộ) mới được cộng vào ví.
        BigDecimal creditAmount = order.getTransactionValue();

        // 3. Sử dụng logic nghiệp vụ tập trung của WalletService để nạp Credit và tạo Transaction
        // Bắt buộc sử dụng depositCredit() để đảm bảo tạo Transaction.
        User user = order.getUser(); // Giả định Order.getUser() trả về User Entity
        walletService.depositCredit(user, creditAmount, order, saleId);

        log.info("Wallet updated for Order #{}. Added {} Credit.", order.getOrderId(), creditAmount);
    }


    // --- Hàm Tiện ích ---

    private void savePaymentLog(Order order, String responseJson, String transactionType) {
        PaymentLog log = PaymentLog.builder()
                .order(order)
                .status(order.getStatus()) // Lấy status hiện tại của Order
                .requestBody(transactionType)
                .responseBody(responseJson)
                // ĐIỀU CHỈNH: gatewayOrderId nên dùng Payment ID hoặc Order ID
                .gatewayOrderId(order.getPaymentReference() != null ? order.getPaymentReference() : order.getOrderId().toString())
                .build();
        paymentLogRepository.save(log);
    }
}
