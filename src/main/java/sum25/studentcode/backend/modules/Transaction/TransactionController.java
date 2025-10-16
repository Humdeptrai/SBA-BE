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

//    @PostMapping
//    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
//        return transactionService.createTransaction(request);
//    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

//    @PutMapping("/{id}")
//    public TransactionResponse updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest request) {
//        return transactionService.updateTransaction(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteTransaction(@PathVariable Long id) {
//        transactionService.deleteTransaction(id);
//    }
}