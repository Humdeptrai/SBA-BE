package sum25.studentcode.backend.modules.MatrixQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.MatrixQuestion;
import sum25.studentcode.backend.model.Questions;

public interface MatrixQuestionRepository extends JpaRepository<MatrixQuestion, Long> {
    boolean existsByMatrixAndQuestion(Matrix matrix, Questions question);

    int countByMatrix(Matrix matrix);
}
