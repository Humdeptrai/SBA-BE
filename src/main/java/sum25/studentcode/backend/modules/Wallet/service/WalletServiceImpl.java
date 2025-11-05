package sum25.studentcode.backend.modules.Wallet.service;

import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Wallet.dto.response.WalletResponse;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;
import sum25.studentcode.backend.modules.Transaction.repository.TransactionRepository; // Import TransactionRepository

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository; // Cần thiết để ghi sổ cái (Ledger)

    // --- Các phương thức Khởi tạo và Truy vấn (READ Operations) ---

    /**
     * Khởi tạo Ví (Chỉ nên dùng nội bộ khi User đăng ký hoặc tạo thủ công bởi Admin).
     */
    @Override
    @Transactional
    public WalletResponse initializeNewWallet(User user) {
        if (walletRepository.findByUser(user) != null) {
            throw new RuntimeException("Wallet already exists for user: " + user.getUserId());
        }

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .currency("VND")
                .isActive(true)
                .build();

        wallet = walletRepository.save(wallet);
        log.info(" Initialized new wallet {} for user {}", wallet.getWalletId(), user.getUserId());
        return convertToResponse(wallet);
    }

    @Override
    public WalletResponse getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + id));
        return convertToResponse(wallet);
    }
    

    @Override
    @Transactional
    public WalletResponse updateBalance(Long userId, BigDecimal amountToAdd) {
        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal newBalance = wallet.getBalance().add(amountToAdd);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        return convertToResponse(wallet);
    }

    @Override
    public Wallet consumeCredit(Long userId, BigDecimal amountToDeduct) {
        // 1. Lấy Wallet của người dùng
        Wallet wallet = getWalletEntityByUserId(userId);

        // 2. Kiểm tra amountToDeduct phải là số dương
        if (amountToDeduct.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount to deduct must be a positive value.");
        }

        // 3. Kiểm tra số dư
        BigDecimal currentBalance = wallet.getBalance();
        if (currentBalance.compareTo(amountToDeduct) < 0) {
            log.warn(" Insufficient balance for user {}. Current: {}, Deduct: {}", userId, currentBalance, amountToDeduct);
            throw new RuntimeException("Insufficient balance to consume credit.");
        }

        // 4. Trừ Credit
        BigDecimal newBalance = currentBalance.subtract(amountToDeduct);
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        log.info(" Credit consumed for user {}. Old balance: {}, New balance: {}", userId, currentBalance, newBalance);
        return wallet;
    }


    // Dành cho xử lý nội bộ (PayPal, Momo, ...)
    @Override
    public Wallet getWalletEntityByUserId(Long userId) {
        // Sửa lại để lấy ví theo userId, không phải walletId
        return walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    public List<WalletResponse> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return user;
    }

    // --- Chức năng LÕI: Thay đổi số dư (Deposit) ---

    /**
     * Nạp Credit vào Ví. Đây là phương thức duy nhất được phép thay đổi số dư.
     * Mọi thay đổi số dư đều phải tạo một bản ghi Transaction.
     * * @param user Đối tượng User
     * @param amount Số tiền Credit nạp vào
     * @param order Order liên quan (mua gói Credit)
     * @param externalRefId Mã giao dịch từ cổng thanh toán (MoMo/VNPay)
     * @return Bản ghi Transaction đã được tạo
     */
    @Override
    @Transactional
    public Transaction depositCredit(User user, BigDecimal amount, Order order, String externalRefId) {
        // 1. Idempotency check - skip if transaction already exists
        if (externalRefId != null && transactionRepository.existsByExternalReferenceId(externalRefId)) {
            log.warn("Transaction with externalRefId {} already exists. Skipping duplicate.", externalRefId);
            return transactionRepository.findByExternalReferenceId(externalRefId).orElse(null);
        }

        // 2. Find or create wallet for user
        Wallet wallet = walletRepository.findByUser(user);
        if (wallet == null) {
            // Create new wallet if it doesn't exist
            wallet = Wallet.builder()
                    .user(user)
                    .balance(BigDecimal.ZERO)
                    .currency("VND")
                    .isActive(true)
                    .build();
            wallet = walletRepository.save(wallet);
            log.info("Created new wallet {} for user {}", wallet.getWalletId(), user.getUserId());
        }

        BigDecimal balanceBefore = wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // 3. Update Wallet (always wrapped in @Transactional)
        wallet.setBalance(balanceAfter);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        log.info("Wallet {} updated: {} -> {}", wallet.getWalletId(), balanceBefore, balanceAfter);

        // 4. Create Transaction (Accounting proof)
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .user(user)
                .order(order)
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description("Nạp credit từ gói Pack #" + order.getRelatedEntityId())
                .externalReferenceId(externalRefId)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Created transaction {} for deposit of {} credits to user {}",
                savedTransaction.getTransactionId(), amount, user.getUserId());

        return savedTransaction;
    }


    @Override
    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }

    @Override
    public WalletResponse getWalletsByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));
        return convertToResponse(wallet);
    }

    @Override
    public boolean transactionExistsForPayment(String externalPaymentId) {
        if (externalPaymentId == null) {
            return false;
        }
        return transactionRepository.existsByExternalReferenceId(externalPaymentId);
    }
    
    // --- Helper (Giữ nguyên) ---

    private WalletResponse convertToResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setWalletId(wallet.getWalletId());
        response.setUserId(wallet.getUser().getUserId());
        response.setBalance(wallet.getBalance());
        response.setCurrency(wallet.getCurrency());
        response.setIsActive(wallet.getIsActive());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());
        return response;
    }
}

