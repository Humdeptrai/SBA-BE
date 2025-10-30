package sum25.studentcode.backend.modules.PracticeSession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.PracticeSession;

import java.util.List;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
    List<PracticeSession> findAllBySessionCode(String sessionCode);
    boolean existsByLesson_LessonIdAndSessionNameIgnoreCase(Long lessonId, String sessionName);
    List<PracticeSession> findByLesson_LessonId(Long lessonId);
}