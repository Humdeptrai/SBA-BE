package sum25.studentcode.backend.modules.PracticeSession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sum25.studentcode.backend.model.PracticeSession;

import java.util.List;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
    List<PracticeSession> findAllBySessionCode(String sessionCode);

    List<PracticeSession> findAllByCreatedBy_UserId(Long createdByUserId);
//    value = "select session_id from practice_session JOIN student_practice ON practice_session.session_id = student_practice.practice_id WHERE student_practice.student_id = ?1", nativeQuery = true
    @Query("select  n.sessionId from PracticeSession n JOIN StudentPractice  p ON n.sessionId = p.practiceSession.sessionId WHERE p.student.userId = ?1")
    List<Long> findSessionIdsByStudentId(Long studentId);

    @Query("select  n.sessionId from PracticeSession n JOIN StudentPractice  p ON n.sessionId = p.practiceSession.sessionId WHERE p.practiceSession.sessionId = ?1")
    List<Long> fetchAttemptTime(Long sessionId);

    @Query("SELECT COUNT(p) FROM StudentPractice p WHERE p.student.userId = ?1 AND p.practiceSession.sessionId = ?2")
    int countAttemptsByStudentAndSession(Long studentId, Long sessionId);
}