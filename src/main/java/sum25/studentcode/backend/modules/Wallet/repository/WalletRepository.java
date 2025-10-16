package sum25.studentcode.backend.modules.Wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.model.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUser(User user);
    Optional<Wallet> findByUser_UserId(Long userId);
}