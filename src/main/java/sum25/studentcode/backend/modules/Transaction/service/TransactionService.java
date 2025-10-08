package sum25.studentcode.backend.modules.Transaction.service;

import sum25.studentcode.backend.modules.Transaction.dto.request.TransactionRequest;
import sum25.studentcode.backend.modules.Transaction.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransactionById(Long id);
    List<TransactionResponse> getAllTransactions();
    TransactionResponse updateTransaction(Long id, TransactionRequest request);
    void deleteTransaction(Long id);
}