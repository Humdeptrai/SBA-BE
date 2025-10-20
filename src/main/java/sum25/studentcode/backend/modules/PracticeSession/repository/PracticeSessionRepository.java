package sum25.studentcode.backend.modules.PracticeSession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.PracticeSession;

import java.util.Optional;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
    Optional<PracticeSession> findBySessionCode(String sessionCode);

}