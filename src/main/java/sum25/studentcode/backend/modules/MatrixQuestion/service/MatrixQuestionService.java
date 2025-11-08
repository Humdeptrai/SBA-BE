package sum25.studentcode.backend.modules.MatrixQuestion.service;

import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionWithOptionsResponse;

import java.util.List;

public interface MatrixQuestionService {

    List<MatrixQuestionWithOptionsResponse> addQuestionsToMatrix(MatrixQuestionRequest request);

    List<MatrixQuestionWithOptionsResponse> getQuestionsWithOptionsByMatrixId(Long matrixId);

    List<MatrixQuestionWithOptionsResponse> getAllMatrixQuestions();

    MatrixQuestionWithOptionsResponse getMatrixQuestionById(Long id);

    MatrixQuestionWithOptionsResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request);

    void deleteMatrixQuestion(Long id);
}

