package sum25.studentcode.backend.modules.Exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Exam;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsByExamCode(String examCode);
    boolean existsByExamName(String examName);
    List<Exam> findByLesson_LessonId(Long lessonId);

}