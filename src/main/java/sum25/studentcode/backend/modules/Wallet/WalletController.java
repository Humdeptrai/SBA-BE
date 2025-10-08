package sum25.studentcode.backend.modules.Wallet;

import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public WalletResponse createWallet(@RequestBody WalletRequest request) {
        return walletService.createWallet(request);
    }

    @GetMapping("/{id}")
    public WalletResponse getWalletById(@PathVariable Long id) {
        return walletService.getWalletById(id);
    }

    @GetMapping
    public List<WalletResponse> getAllWallets() {
        return walletService.getAllWallets();
    }

    @PutMapping("/{id}")
    public WalletResponse updateWallet(@PathVariable Long id, @RequestBody WalletRequest request) {
        return walletService.updateWallet(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
    }
}