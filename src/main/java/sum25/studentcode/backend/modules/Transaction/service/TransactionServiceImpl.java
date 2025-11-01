package sum25.studentcode.backend.modules.Transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Order;
import sum25.studentcode.backend.model.Transaction;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Payment.repository.OrderRepository;
import sum25.studentcode.backend.modules.Transaction.dto.request.TransactionRequest;
import sum25.studentcode.backend.modules.Transaction.dto.response.TransactionResponse;
import sum25.studentcode.backend.modules.Transaction.repository.TransactionRepository;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId())
                    .orElse(null);
        }

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .user(user)
                .order(order)
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .balanceBefore(request.getBalanceBefore())
                .balanceAfter(request.getBalanceAfter())
                .description(request.getDescription())
                .status(request.getStatus())
                .externalReferenceId(request.getExternalReferenceId())
                .build();

        transaction = transactionRepository.save(transaction);
        return convertToResponse(transaction);
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return convertToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId()).orElse(null);
        }

        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setOrder(order);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(request.getBalanceBefore());
        transaction.setBalanceAfter(request.getBalanceAfter());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(request.getStatus());
        transaction.setExternalReferenceId(request.getExternalReferenceId());

        transaction = transactionRepository.save(transaction);
        return convertToResponse(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public Transaction createDepositTransaction(Wallet wallet, User user, BigDecimal amount,
                                                BigDecimal before, BigDecimal after,
                                                String externalReferenceId) {
        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .user(user)
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(before)
                .balanceAfter(after)
                .description("Nạp tiền qua PayPal")
                .status(Transaction.TransactionStatus.SUCCESS)
                .externalReferenceId(externalReferenceId)
                .build();
        return transactionRepository.save(txn);
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        // Verify user exists first
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Get all transactions for the user
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);

        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .walletId(transaction.getWallet().getWalletId())
                .userId(transaction.getUser().getUserId())
                .orderId(transaction.getOrder() != null ? transaction.getOrder().getOrderId() : null)
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .externalReferenceId(transaction.getExternalReferenceId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
