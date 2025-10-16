package sum25.studentcode.backend.modules.Transaction.service;

import sum25.studentcode.backend.model.Transaction;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Transaction.dto.request.TransactionRequest;
import sum25.studentcode.backend.modules.Transaction.dto.response.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransactionById(Long id);
    List<TransactionResponse> getAllTransactions();
    TransactionResponse updateTransaction(Long id, TransactionRequest request);
    void deleteTransaction(Long id);
    Transaction createDepositTransaction(Wallet wallet, User user, BigDecimal amount,
                                         BigDecimal before, BigDecimal after,
                                         String externalReferenceId);
}