package sum25.studentcode.backend.modules.MatrixQuestion.service;

import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionResponse;

import java.util.List;

public interface MatrixQuestionService {
    List<MatrixQuestionResponse> addQuestionsToMatrix(MatrixQuestionRequest request);
    MatrixQuestionResponse getMatrixQuestionById(Long id);
    List<MatrixQuestionResponse> getAllMatrixQuestions();
    MatrixQuestionResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request);
    void deleteMatrixQuestion(Long id);
}