package sum25.studentcode.backend.modules.StudentAnswers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.StudentAnswers;

public interface StudentAnswersRepository extends JpaRepository<StudentAnswers, Long> {
}