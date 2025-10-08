package sum25.studentcode.backend.modules.Transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Transaction;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Transaction.dto.request.TransactionRequest;
import sum25.studentcode.backend.modules.Transaction.dto.response.TransactionResponse;
import sum25.studentcode.backend.modules.Transaction.repository.TransactionRepository;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .user(user)
                .transactionType(request.getTransactionType())
                .amount(request.getAmount())
                .balanceBefore(request.getBalanceBefore())
                .balanceAfter(request.getBalanceAfter())
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .status(request.getStatus())
                .transactionDate(request.getTransactionDate())
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
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(request.getBalanceBefore());
        transaction.setBalanceAfter(request.getBalanceAfter());
        transaction.setDescription(request.getDescription());
        transaction.setReferenceId(request.getReferenceId());
        transaction.setStatus(request.getStatus());
        transaction.setTransactionDate(request.getTransactionDate());
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

    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setWalletId(transaction.getWallet().getWalletId());
        response.setUserId(transaction.getUser().getUserId());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setBalanceBefore(transaction.getBalanceBefore());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setReferenceId(transaction.getReferenceId());
        response.setStatus(transaction.getStatus());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        return response;
    }
}