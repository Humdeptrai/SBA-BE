package sum25.studentcode.backend.modules.MatrixQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.MatrixQuestion;

public interface MatrixQuestionRepository extends JpaRepository<MatrixQuestion, Long> {
}