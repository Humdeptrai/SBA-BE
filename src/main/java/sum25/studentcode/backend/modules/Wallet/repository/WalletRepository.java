package sum25.studentcode.backend.modules.Wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}