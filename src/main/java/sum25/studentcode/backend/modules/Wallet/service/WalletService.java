package sum25.studentcode.backend.modules.Wallet.service;

import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Transaction;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Wallet.dto.response.WalletResponse;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    // Giao diện chỉ cho phép READ cho mục đích hiển thị
    WalletResponse getWalletById(Long id);


    WalletResponse updateBalance(Long userId, BigDecimal amountToAdd);

    Wallet consumeCredit (Long userId, BigDecimal amountToDeduct);
    List<WalletResponse> getAllWallets();


    Wallet getWalletEntityByUserId(Long userId); // Trả về thực thể Wallet để thao tác nội bộ

    User getUserByUsername(String username);
    // HẠN CHẾ: Chỉ cho phép tạo ví tự động khi người dùng đăng ký
    WalletResponse initializeNewWallet(User user);

    void save(Wallet wallet); // Dành cho nội bộ

    WalletResponse getWalletsByUserId(Long userId);



    // CHỨC NĂNG CỘT LÕI: Thay đổi số dư được kiểm soát (Deposit/Withdraw)

    /**
     * Nạp tiền (Cộng Credit). Bắt buộc phải tạo Transaction.
     * @return Bản ghi Transaction đã được tạo.
     */
    Transaction depositCredit(User user, BigDecimal amount, Order order, String externalRefId);

    /**
     * Check if a transaction already exists for the given external payment ID.
     * Used for idempotency checking.
     */
    boolean transactionExistsForPayment(String externalPaymentId);

    // Transaction withdrawCredit(User user, BigDecimal amount, Order order); // Tương lai: cho luồng sử dụng dịch vụ
}