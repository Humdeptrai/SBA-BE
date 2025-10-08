package sum25.studentcode.backend.modules.Auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
