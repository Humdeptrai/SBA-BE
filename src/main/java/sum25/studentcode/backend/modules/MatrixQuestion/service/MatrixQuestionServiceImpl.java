package sum25.studentcode.backend.modules.MatrixQuestion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Level;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.MatrixQuestion;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionResponse;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrixQuestionServiceImpl implements MatrixQuestionService {

    private final MatrixQuestionRepository matrixQuestionRepository;
    private final MatrixRepository matrixRepository;
    private final QuestionsRepository questionsRepository;

    /**
     * ✅ Thêm 1 hoặc nhiều câu hỏi vào ma trận
     * Nếu questionIds chỉ có 1 phần tử thì vẫn hoạt động như thêm 1 câu hỏi.
     * Nếu marksAllocated bị null → tự động lấy điểm từ Level.difficultyScore.
     */
    @Transactional
    @Override
    public List<MatrixQuestionResponse> addQuestionsToMatrix(MatrixQuestionRequest request) {
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException(
                        "MATRIX_NOT_FOUND",
                        "Không tìm thấy ma trận đề thi.",
                        404
                ));

        if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
            throw new ApiException(
                    "QUESTION_IDS_EMPTY",
                    "Danh sách câu hỏi không được để trống.",
                    400
            );
        }

        int currentCount = matrixQuestionRepository.countByMatrix(matrix);
        int total = matrix.getTotalQuestions() != null ? matrix.getTotalQuestions() : 0;

        if (total > 0 && currentCount + request.getQuestionIds().size() > total) {
            throw new ApiException(
                    "MATRIX_EXCEED",
                    String.format("Số câu hỏi vượt quá giới hạn (%d hiện có + %d thêm mới > %d).",
                            currentCount, request.getQuestionIds().size(), total),
                    400
            );
        }

        // ⚠️ Nếu chưa đủ thì chỉ cảnh báo nhẹ
        if (total > 0 && currentCount + request.getQuestionIds().size() < total) {
            throw new ApiException(
                    "MATRIX_INCOMPLETE",
                    String.format("Ma trận hiện mới có %d/%d câu hỏi. Vui lòng bổ sung thêm.",
                            currentCount + request.getQuestionIds().size(), total),
                    206
            );
        }

        // ✅ Thêm câu hỏi
        return request.getQuestionIds().stream()
                .map(qId -> {
                    Questions question = questionsRepository.findById(qId)
                            .orElseThrow(() -> new ApiException(
                                    "QUESTION_NOT_FOUND",
                                    "Không tìm thấy câu hỏi ID=" + qId,
                                    404
                            ));

                    boolean exists = matrixQuestionRepository.existsByMatrixAndQuestion(matrix, question);
                    if (exists) {
                        throw new ApiException(
                                "DUPLICATE_QUESTION",
                                "Câu hỏi ID=" + qId + " đã tồn tại trong ma trận này.",
                                400
                        );
                    }

                    // ✅ Lấy điểm theo Level nếu không truyền marksAllocated
                    BigDecimal marks = request.getMarksAllocated();
                    if (marks == null || marks.compareTo(BigDecimal.ZERO) <= 0) {
                        Level level = question.getLevel();
                        if (level == null) {
                            throw new ApiException(
                                    "LEVEL_NOT_FOUND",
                                    "Câu hỏi ID=" + qId + " chưa được gán cấp độ, không thể xác định điểm.",
                                    400
                            );
                        }
                        marks = BigDecimal.valueOf(level.getDifficultyScore());
                    }

                    MatrixQuestion mq = MatrixQuestion.builder()
                            .matrix(matrix)
                            .question(question)
                            .marksAllocated(marks)
                            .build();

                    matrixQuestionRepository.save(mq);
                    return convertToResponse(mq);
                })
                .collect(Collectors.toList());
    }

    /** ✅ Lấy 1 MatrixQuestion theo ID */
    @Override
    public MatrixQuestionResponse getMatrixQuestionById(Long id) {
        MatrixQuestion matrixQuestion = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "MATRIX_QUESTION_NOT_FOUND",
                        "Không tìm thấy câu hỏi trong ma trận.",
                        404
                ));
        return convertToResponse(matrixQuestion);
    }

    /** ✅ Lấy tất cả các MatrixQuestion */
    @Override
    public List<MatrixQuestionResponse> getAllMatrixQuestions() {
        List<MatrixQuestion> list = matrixQuestionRepository.findAll();
        if (list.isEmpty()) {
            throw new ApiException("EMPTY_LIST", "Chưa có câu hỏi nào trong ma trận.", 404);
        }
        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * ✅ Cập nhật marksAllocated hoặc gán lại câu hỏi khác
     * Nếu marksAllocated trống → tự động lấy theo Level của câu hỏi.
     */
    @Override
    public MatrixQuestionResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request) {
        MatrixQuestion matrixQuestion = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "MATRIX_QUESTION_NOT_FOUND",
                        "Không tìm thấy câu hỏi trong ma trận.",
                        404
                ));

        if (request.getMatrixId() != null) {
            Matrix matrix = matrixRepository.findById(request.getMatrixId())
                    .orElseThrow(() -> new ApiException(
                            "MATRIX_NOT_FOUND",
                            "Không tìm thấy ma trận đề thi.",
                            404
                    ));
            matrixQuestion.setMatrix(matrix);
        }

        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            Long qId = request.getQuestionIds().get(0);
            Questions question = questionsRepository.findById(qId)
                    .orElseThrow(() -> new ApiException(
                            "QUESTION_NOT_FOUND",
                            "Không tìm thấy câu hỏi ID=" + qId,
                            404
                    ));
            matrixQuestion.setQuestion(question);
        }

        // ✅ Nếu không truyền điểm → lấy theo Level của câu hỏi
        BigDecimal marks = request.getMarksAllocated();
        if (marks == null || marks.compareTo(BigDecimal.ZERO) <= 0) {
            Questions question = matrixQuestion.getQuestion();
            if (question.getLevel() == null) {
                throw new ApiException(
                        "LEVEL_NOT_FOUND",
                        "Câu hỏi ID=" + question.getQuestionId() + " chưa được gán cấp độ, không thể xác định điểm.",
                        400
                );
            }
            marks = BigDecimal.valueOf(question.getLevel().getDifficultyScore());
        }

        matrixQuestion.setMarksAllocated(marks);
        matrixQuestionRepository.save(matrixQuestion);
        return convertToResponse(matrixQuestion);
    }

    /** ✅ Xóa 1 MatrixQuestion */
    @Override
    public void deleteMatrixQuestion(Long id) {
        if (!matrixQuestionRepository.existsById(id)) {
            throw new ApiException("MATRIX_QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi trong ma trận.", 404);
        }
        matrixQuestionRepository.deleteById(id);
    }

    /** ✅ Chuyển entity sang response */
    private MatrixQuestionResponse convertToResponse(MatrixQuestion entity) {
        MatrixQuestionResponse res = new MatrixQuestionResponse();
        res.setMatrixQuestionId(entity.getMatrixQuestionId());
        res.setMatrixId(entity.getMatrix().getMatrixId());
        res.setMatrixName(entity.getMatrix().getMatrixName());
        res.setQuestionId(entity.getQuestion().getQuestionId());
        res.setQuestionText(entity.getQuestion().getQuestionText());
        res.setMarksAllocated(entity.getMarksAllocated());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        return res;
    }
}
