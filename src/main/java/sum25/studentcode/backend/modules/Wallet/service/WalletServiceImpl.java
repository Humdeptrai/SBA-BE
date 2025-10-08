package sum25.studentcode.backend.modules.Wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Wallet.dto.request.WalletRequest;
import sum25.studentcode.backend.modules.Wallet.dto.response.WalletResponse;
import sum25.studentcode.backend.modules.Wallet.repository.WalletRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public WalletResponse createWallet(WalletRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .isActive(request.getIsActive())
                .build();
        wallet = walletRepository.save(wallet);
        return convertToResponse(wallet);
    }

    @Override
    public WalletResponse getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return convertToResponse(wallet);
    }

    @Override
    public List<WalletResponse> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WalletResponse updateWallet(Long id, WalletRequest request) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        wallet.setUser(user);
        wallet.setBalance(request.getBalance());
        wallet.setCurrency(request.getCurrency());
        wallet.setIsActive(request.getIsActive());
        wallet = walletRepository.save(wallet);
        return convertToResponse(wallet);
    }

    @Override
    public void deleteWallet(Long id) {
        if (!walletRepository.existsById(id)) {
            throw new RuntimeException("Wallet not found");
        }
        walletRepository.deleteById(id);
    }

    private WalletResponse convertToResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setWalletId(wallet.getWalletId());
        response.setUserId(wallet.getUser().getUserId());
        response.setBalance(wallet.getBalance());
        response.setCurrency(wallet.getCurrency());
        response.setIsActive(wallet.getIsActive());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());
        return response;
    }
}