package sum25.studentcode.backend.modules.Wallet.service;

import sum25.studentcode.backend.modules.Wallet.dto.request.WalletRequest;
import sum25.studentcode.backend.modules.Wallet.dto.response.WalletResponse;

import java.util.List;

public interface WalletService {
    WalletResponse createWallet(WalletRequest request);
    WalletResponse getWalletById(Long id);
    List<WalletResponse> getAllWallets();
    WalletResponse updateWallet(Long id, WalletRequest request);
    void deleteWallet(Long id);
}