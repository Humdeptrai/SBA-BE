package sum25.studentcode.backend.modules.StudentAnswers.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.model.StudentAnswers;
import sum25.studentcode.backend.model.StudentPractice;

import java.util.List;

public interface StudentAnswersRepository extends JpaRepository<StudentAnswers, Long> {
    List<StudentAnswers> findByStudentPractice_PracticeId(Long practiceId);

    // ✅ Lấy tất cả đáp án của 1 practice
    List<StudentAnswers> findByStudentPractice(StudentPractice practice);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<StudentAnswers> findAllByStudentPracticeAndQuestion(StudentPractice studentPractice, Questions question);

    @Query("""
    SELECT sa FROM StudentAnswers sa
    WHERE sa.answeredAt = (
        SELECT MAX(sa2.answeredAt)
        FROM StudentAnswers sa2
        WHERE sa2.studentPractice.practiceId = sa.studentPractice.practiceId
          AND sa2.question.questionId = sa.question.questionId
    )
    """)
    List<StudentAnswers> findLatestAnswersByPracticeId(@Param("practiceId") Long practiceId);

    @Query("""
    SELECT sa FROM StudentAnswers sa
    JOIN FETCH sa.question q
    WHERE sa.isCorrect IS NOT NULL
    """)
    List<StudentAnswers> findAllWithQuestions();

@Query("""
        SELECT u.username, ps.sessionName, q.questionText, o.optionText, sa.isCorrect
        FROM StudentAnswers sa
        JOIN sa.studentPractice sp
        JOIN sp.practiceSession ps
        JOIN sp.student u
        JOIN sa.question q
        LEFT JOIN Options o ON o.optionId = sa.selectedOptionId
        WHERE (:sessionId IS NULL OR ps.sessionCode = :sessionId)
        ORDER BY u.username, ps.sessionName, q.questionText
        """)
    List<Object[]> findStudentAnswerDetails(@Param("sessionId") String sessionId);
}
