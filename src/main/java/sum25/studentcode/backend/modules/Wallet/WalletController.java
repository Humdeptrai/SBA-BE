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

    @GetMapping("/{id}")
    public WalletResponse getWalletById(@PathVariable Long id) {
        return walletService.getWalletById(id);
    }

    @GetMapping
    public List<WalletResponse> getAllWallets() {
        return walletService.getAllWallets();
    }

}