package sum25.studentcode.backend.modules.Transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Transaction.dto.request.TransactionRequest;
import sum25.studentcode.backend.modules.Transaction.dto.response.TransactionResponse;
import sum25.studentcode.backend.modules.Transaction.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/user/{userId}")
    public List<TransactionResponse> getTransactionsByUserId(@PathVariable Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }

}