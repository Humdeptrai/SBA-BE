package sum25.studentcode.backend.modules.StudentAnswers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.StudentAnswers;

import java.util.List;

public interface StudentAnswersRepository extends JpaRepository<StudentAnswers, Long> {
    List<StudentAnswers> findByStudentPractice_PracticeId(Long practiceId);

}