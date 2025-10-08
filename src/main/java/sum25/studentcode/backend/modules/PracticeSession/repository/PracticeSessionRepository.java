package sum25.studentcode.backend.modules.PracticeSession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.PracticeSession;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
}