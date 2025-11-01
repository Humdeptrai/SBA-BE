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
import org.springframework.transaction.annotation.Transactional;
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
import java.time.LocalDateTime;
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
     * Hỗ trợ tự động chuyển đổi từ VNĐ sang USD để tương thích với PayPal Sandbox.
     */
    public PayPalPaymentResponse createPaymentRequest(PackPurchaseRequest request, Long userId) {
        // 1️⃣ Lấy thông tin pack
        Pack pack = packRepository.findById(request.getPackId())
                .orElseThrow(() -> new RuntimeException("Pack not found: " + request.getPackId()));

        // 2️⃣ Kiểm tra giá trị gói
        BigDecimal packValueVnd = pack.getPackValue();
        if (packValueVnd == null || packValueVnd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid pack value: " + packValueVnd);
        }

        // ⚙️ Tỷ giá quy đổi VNĐ → USD (tuỳ chỉnh)
        BigDecimal exchangeRate = new BigDecimal("23000"); // 1 USD = 23,000 VNĐ
        BigDecimal packValueUsd = packValueVnd.divide(exchangeRate, 2, java.math.RoundingMode.HALF_UP);

        // Log thông tin để debug
        log.info("🧾 Creating PayPal payment | Pack ID: {} | User ID: {} | Value: {} VND (~{} USD)",
                pack.getPackId(), userId, packValueVnd, packValueUsd);

        // 3️⃣ Tạo đơn hàng trạng thái PENDING
        Order order = orderService.createPendingOrder(userId, pack.getPackId());

        // 4️⃣ Chuẩn bị dữ liệu thanh toán PayPal
        Amount amount = new Amount();
        amount.setCurrency("USD"); // Sandbox chỉ hỗ trợ các loại tiền như USD, EUR, GBP, ...
        amount.setTotal(String.format("%.2f", packValueUsd)); // Đảm bảo format hợp lệ 2 chữ số thập phân

        com.paypal.api.payments.Transaction transaction = new com.paypal.api.payments.Transaction();
        transaction.setDescription("Purchase pack " + pack.getPackId());
        transaction.setAmount(amount);
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
            // 5️⃣ Gọi API PayPal tạo Payment
            Payment createdPayment = payment.create(apiContext);

            // 6️⃣ Lấy URL người dùng cần redirect
            String approvalUrl = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElse(null);

            // 7️⃣ Cập nhật Order và lưu log
            order.setPaymentReference(createdPayment.getId());
            orderService.saveOrder(order);
            savePaymentLog(order, createdPayment.toJSON(), "CREATE_PAYMENT_REQUEST");

            log.info("✅ PayPal payment created successfully | PaymentID={} | ApprovalUrl={}",
                    createdPayment.getId(), approvalUrl);

            // 8️⃣ Trả về phản hồi cho FE
            return PayPalPaymentResponse.builder()
                    .paymentId(createdPayment.getId())
                    .approvalUrl(approvalUrl)
                    .amount(amount.getTotal())
                    .currency(amount.getCurrency())
                    .status(createdPayment.getState())
                    .build();

        } catch (PayPalRESTException e) {
            log.error(" Error creating PayPal payment for Order {}: {}", order.getOrderId(), e.getMessage());
            orderService.failOrder(order);
            throw new RuntimeException("Payment creation failed: " + e.getMessage());
        }
    }


    /**
     * 2. Thực hiện Payment và Xác nhận (Bước 2: Execute Payment).
     * Hàm này chỉ gọi API PayPal để hoàn tất giao dịch. KHÔNG CẬP NHẬT WALLET/TRANSACTION.
     * @return OrderId liên quan
     */
    /**
     * 2. Thực hiện Payment và Xác nhận (Bước 2: Execute Payment).
     * Hàm này chỉ gọi API PayPal để hoàn tất giao dịch. KHÔNG CẬP NHẬT WALLET/TRANSACTION.
     * @return OrderId liên quan
     */
    public Long executePaymentAndVerify(String paymentId, String payerId) throws PayPalRESTException {
        //  Nếu log đã tồn tại, bỏ qua việc lưu trùng
        if (paymentLogRepository.existsByGatewayOrderId(paymentId)) {
            log.warn("️ Payment log already exists for paymentId {}. Skipping duplicate save.", paymentId);

            PaymentLog existing = paymentLogRepository.findByGatewayOrderId(paymentId);
            Order existingOrder = existing.getOrder();
            if (existingOrder != null) {
                return existingOrder.getOrderId();
            }

            // Nếu chưa có Order gắn, fallback truy vấn qua OrderService
            Order order = orderService.getOrderByPaymentReference(paymentId);
            return order.getOrderId();
        }

        //  Gọi PayPal API để hoàn tất thanh toán
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, execution);

        //  Lấy Order tương ứng
        Order order = orderService.getOrderByPaymentReference(paymentId);

        //  Ghi log an toàn (sẽ không trùng nhờ check ở trên)
        savePaymentLog(order, executedPayment.toJSON(), "EXECUTE_PAYMENT_RESPONSE");

        //  Nếu approved → return orderId
        if ("approved".equalsIgnoreCase(executedPayment.getState())) {
            String invoiceNumber = executedPayment.getTransactions().get(0).getInvoiceNumber();
            if (invoiceNumber == null) {
                throw new RuntimeException("Missing Invoice Number for PayPal Payment " + paymentId);
            }
            return Long.valueOf(invoiceNumber);
        } else {
            orderService.failOrder(order);
            throw new PayPalRESTException("Payment execution failed. State: " + executedPayment.getState());
        }
    }


    /**
     * 3. Xử lý Webhook (IPN) từ PayPal (Bước 4: Cộng tiền an toàn).
     * Logic cộng tiền, tạo Transaction chỉ được thực hiện tại đây.
     */
    public void processWebhook(String webhookEvent, Map<String, String> headers) throws Exception {
        log.info("🔔 Received PayPal webhook. Event size: {} chars", webhookEvent.length());

        try {
            // 1. CHƯA TRIỂN KHAI: Xác thực Webhook.
            // Trong môi trường sản phẩm, bước này là BẮT BUỘC để đảm bảo tính hợp lệ của request.
            // Cần gọi API verifyWebhookSignature của PayPal.
            // Tạm thời bỏ qua bước xác thực để tập trung vào logic cốt lõi.

            // 2. Phân tích cú pháp sự kiện
            WebhookEventPayload event = objectMapper.readValue(webhookEvent, WebhookEventPayload.class);
            String eventType = event.getEventType();
            log.info("📋 Processing Webhook Event Type: {}", eventType);

            if ("PAYMENT.SALE.COMPLETED".equals(eventType)) {
                log.info("💰 Processing PAYMENT.SALE.COMPLETED event");

                // Sự kiện báo giao dịch đã hoàn tất và tiền đã về Merchant
                Sale sale = objectMapper.convertValue(event.getResource(), Sale.class);
                String paymentId = sale.getParentPayment();
                String saleId = sale.getId();

                log.info("🔍 Looking for order with paymentReference: {}, saleId: {}", paymentId, saleId);

                try {
                    // Lấy Order từ paymentReference
                    Order order = orderService.getOrderByPaymentReference(paymentId);
                    log.info("📦 Found order: {} with status: {}", order.getOrderId(), order.getStatus());

                    if (order.getStatus() != Order.OrderStatus.COMPLETED) {
                        // Lấy thông tin giá trị (USD from PayPal)
                        BigDecimal amount = new BigDecimal(sale.getAmount().getTotal());
                        log.info("💵 Processing payment amount: {} USD for order: {}", amount, order.getOrderId());

                        // 3. Thực hiện cộng Credit và ghi Transaction
                        processSuccessfulOrder(order, amount, paymentId, saleId);

                        log.info("✅ Successfully processed Webhook and completed Order {}.", order.getOrderId());
                    } else {
                        log.warn("⚠️ Webhook for PaymentID {} received, but Order {} is already COMPLETED. Ignoring.",
                                paymentId, order.getOrderId());
                    }
                } catch (Exception orderException) {
                    log.error("❌ Error processing order for paymentId {}: {}", paymentId, orderException.getMessage(), orderException);
                    throw orderException;
                }
            } else {
                log.info("ℹ️ Ignoring webhook event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("❌ Critical error processing webhook: {}", e.getMessage(), e);
            log.error("📄 Webhook payload that failed: {}", webhookEvent);
            throw e;
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


    /**
     * Fallback method to create transaction during success redirect if webhook hasn't been called.
     * This ensures transactions are created even if webhooks fail or are delayed.
     * @param paymentId PayPal payment ID
     * @param payerId PayPal payer ID
     * @return true if transaction was created, false if it already existed
     */
    @Transactional
    public boolean createTransactionIfNotExists(String paymentId, String payerId) {
        try {
            log.info("🔍 Checking if transaction exists for paymentId: {}", paymentId);

            // Get the order by payment reference
            Order order = orderService.getOrderByPaymentReference(paymentId);

            // Check if transaction already exists for this payment
            if (walletService.transactionExistsForPayment(paymentId)) {
                log.info("ℹ️ Transaction already exists for paymentId: {}", paymentId);
                return false;
            }

            // Get payment details from PayPal to get the actual amount paid
            Payment payment = Payment.get(apiContext, paymentId);

            // Find the executed payment transaction
            com.paypal.api.payments.Transaction paypalTransaction = payment.getTransactions().get(0);
            BigDecimal amountPaid = new BigDecimal(paypalTransaction.getAmount().getTotal());

            log.info("💰 Creating fallback transaction for order {} with amount {} USD",
                    order.getOrderId(), amountPaid);

            // Create the transaction using the same logic as webhook
            processSuccessfulOrder(order, amountPaid, paymentId, paymentId + "_fallback");

            return true;

        } catch (Exception e) {
            log.error("❌ Error creating fallback transaction for paymentId {}: {}", paymentId, e.getMessage(), e);
            // Don't throw exception - this is a fallback, main payment already succeeded
            return false;
        }
    }

    // --- Hàm Tiện ích ---

    private void savePaymentLog(Order order, String responseJson, String transactionType) {

        if (paymentLogRepository.existsByGatewayOrderId(order.getPaymentReference())) {
            log.warn("Duplicate log detected for gateway_order_id={}, skip saving.", order.getPaymentReference());
            return;
        }

        PaymentLog log = PaymentLog.builder()
                .order(order)
                .amount(order.getTransactionValue()) // hoặc lấy pack.getPackValue() nếu muốn ghi giá tiền
                .gatewayOrderId(order.getPaymentReference())
                .paymentGateway("PAYPAL")
                .requestBody(transactionType)
                .responseBody(responseJson)
                .responseCode(0)
                .status(order.getStatus())
                .build();

        paymentLogRepository.save(log);
    }
}
