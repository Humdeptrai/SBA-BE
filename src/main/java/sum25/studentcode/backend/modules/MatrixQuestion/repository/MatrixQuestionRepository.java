package sum25.studentcode.backend.modules.MatrixQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.MatrixQuestion;
import sum25.studentcode.backend.model.Questions;

import java.util.List;

public interface MatrixQuestionRepository extends JpaRepository<MatrixQuestion, Long> {
    boolean existsByMatrixAndQuestion(Matrix matrix, Questions question);

    int countByMatrix(Matrix matrix);

    List<MatrixQuestion> findByMatrix_MatrixId(Long matrixId);

    List<MatrixQuestion> findByQuestion_QuestionId(Long questionId);
}

