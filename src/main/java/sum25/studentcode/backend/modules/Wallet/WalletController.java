package sum25.studentcode.backend.modules.Wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Wallet.dto.request.WalletRequest;
import sum25.studentcode.backend.modules.Wallet.dto.response.WalletResponse;
import sum25.studentcode.backend.modules.Wallet.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

//    @GetMapping("/{id}")
//    public ResponseEntity<WalletResponse> getWalletById(@PathVariable Long id) {
//        return  ResponseEntity.ok(walletService.getWalletById(id));
//    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> getAllWallets() {
        return  ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWalletsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletsByUserId(userId));
    }

}