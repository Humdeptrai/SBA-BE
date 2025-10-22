package sum25.studentcode.backend.modules.MatrixQuestion.service;

import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionWithOptionsResponse;
import java.util.List;

public interface MatrixQuestionService {

    /** ✅ Thêm câu hỏi vào ma trận, trả về luôn options */
    List<MatrixQuestionWithOptionsResponse> addQuestionsToMatrix(MatrixQuestionRequest request);

    /** ✅ Lấy danh sách câu hỏi + options của 1 ma trận */
    List<MatrixQuestionWithOptionsResponse> getQuestionsWithOptionsByMatrixId(Long matrixId);

    /** ✅ Lấy toàn bộ câu hỏi trong hệ thống */
    List<MatrixQuestionWithOptionsResponse> getAllMatrixQuestions();

    /** ✅ Lấy chi tiết 1 câu hỏi trong ma trận */
    MatrixQuestionWithOptionsResponse getMatrixQuestionById(Long id);

    /** ✅ Cập nhật câu hỏi trong ma trận */
    MatrixQuestionWithOptionsResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request);

    /** ✅ Xóa 1 câu hỏi khỏi ma trận */
    void deleteMatrixQuestion(Long id);
}
