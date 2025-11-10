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
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionWithOptionsResponse;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrixQuestionServiceImpl implements MatrixQuestionService {

    private final MatrixQuestionRepository matrixQuestionRepository;
    private final MatrixRepository matrixRepository;
    private final OptionsRepository optionsRepository;
    private final QuestionsRepository questionsRepository;

    /**
     * ✅ Thêm 1 hoặc nhiều câu hỏi vào ma trận
     * → Trả về đầy đủ options của mỗi câu hỏi
     */
    @Transactional
    @Override
    public List<MatrixQuestionWithOptionsResponse> addQuestionsToMatrix(MatrixQuestionRequest request) {
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
            throw new ApiException("QUESTION_IDS_EMPTY", "Danh sách câu hỏi không được để trống.", 400);
        }

        int currentCount = matrixQuestionRepository.countByMatrix(matrix);
        int total = matrix.getTotalQuestions() != null ? matrix.getTotalQuestions() : 0;

        if (total > 0 && currentCount + request.getQuestionIds().size() > total) {
            throw new ApiException("MATRIX_EXCEED",
                    String.format("Số câu hỏi vượt quá giới hạn (%d hiện có + %d thêm mới > %d).",
                            currentCount, request.getQuestionIds().size(), total), 400);
        }

        return request.getQuestionIds().stream()
                .map(qId -> {
                    Questions question = questionsRepository.findById(qId)
                            .orElseThrow(() -> new ApiException("QUESTION_NOT_FOUND",
                                    "Không tìm thấy câu hỏi ID=" + qId, 404));

                    if (matrixQuestionRepository.existsByMatrixAndQuestion(matrix, question)) {
                        throw new ApiException("DUPLICATE_QUESTION",
                                "Câu hỏi ID=" + qId + " đã tồn tại trong ma trận này.", 400);
                    }

                    BigDecimal marks = request.getMarksAllocated();
                    if (marks == null || marks.compareTo(BigDecimal.ZERO) <= 0) {
                        Level level = question.getLevel();
                        if (level == null) {
                            throw new ApiException("LEVEL_NOT_FOUND",
                                    "Câu hỏi ID=" + qId + " chưa được gán cấp độ.", 400);
                        }
                        marks = BigDecimal.valueOf(level.getDifficultyScore());
                    }

                    MatrixQuestion mq = matrixQuestionRepository.save(
                            MatrixQuestion.builder()
                                    .matrix(matrix)
                                    .question(question)
                                    .marksAllocated(marks)
                                    .build()
                    );

                    // build response như cũ...
                    MatrixQuestionWithOptionsResponse res = new MatrixQuestionWithOptionsResponse();
                    res.setMatrixQuestionId(mq.getMatrixQuestionId());
                    res.setMatrixId(matrix.getMatrixId());
                    res.setMatrixName(matrix.getMatrixName());
                    res.setQuestionId(question.getQuestionId());
                    res.setQuestionText(question.getQuestionText());
                    res.setMarksAllocated(marks);

                    List<OptionsResponse> options = optionsRepository
                            .findByQuestion_QuestionId(question.getQuestionId())
                            .stream()
                            .map(o -> {
                                OptionsResponse opt = new OptionsResponse();
                                opt.setOptionId(o.getOptionId());
                                opt.setQuestionId(o.getQuestion().getQuestionId());
                                opt.setOptionText(o.getOptionText());
                                opt.setIsCorrect(o.getIsCorrect());
                                opt.setOptionOrder(o.getOptionOrder());
                                opt.setCreatedAt(o.getCreatedAt());
                                opt.setUpdatedAt(o.getUpdatedAt());
                                return opt;
                            }).collect(Collectors.toList());

                    res.setOptions(options);
                    return res;
                })
                .collect(Collectors.toList());
    }


    /** ✅ Lấy danh sách câu hỏi + options trong 1 matrix */
    @Override
    @Transactional(readOnly = true)
    public List<MatrixQuestionWithOptionsResponse> getQuestionsWithOptionsByMatrixId(Long matrixId) {
        List<MatrixQuestion> list = matrixQuestionRepository.findByMatrix_MatrixId(matrixId);
        if (list.isEmpty()) {
            // Trả danh sách rỗng kèm message
            return Collections.emptyList();
        }

        return list.stream().map(mq -> {
            MatrixQuestionWithOptionsResponse res = new MatrixQuestionWithOptionsResponse();
            res.setMatrixQuestionId(mq.getMatrixQuestionId());
            res.setMatrixId(mq.getMatrix().getMatrixId());
            res.setMatrixName(mq.getMatrix().getMatrixName());
            res.setQuestionId(mq.getQuestion().getQuestionId());
            res.setQuestionText(mq.getQuestion().getQuestionText());
            res.setMarksAllocated(mq.getMarksAllocated());

            List<OptionsResponse> options = optionsRepository
                    .findByQuestion_QuestionId(mq.getQuestion().getQuestionId())
                    .stream()
                    .map(o -> {
                        OptionsResponse opt = new OptionsResponse();
                        opt.setOptionId(o.getOptionId());
                        opt.setQuestionId(o.getQuestion().getQuestionId());
                        opt.setOptionText(o.getOptionText());
                        opt.setIsCorrect(o.getIsCorrect());
                        opt.setOptionOrder(o.getOptionOrder());
                        opt.setCreatedAt(o.getCreatedAt());
                        opt.setUpdatedAt(o.getUpdatedAt());
                        return opt;
                    }).collect(Collectors.toList());

            res.setOptions(options);
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteMatrixQuestion(Long id) {
        if (!matrixQuestionRepository.existsById(id)) {
            throw new ApiException("MATRIX_QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi trong ma trận.", 404);
        }
        matrixQuestionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatrixQuestionWithOptionsResponse> getAllMatrixQuestions() {
        List<MatrixQuestion> list = matrixQuestionRepository.findAll();
        if (list.isEmpty()) {
            throw new ApiException("EMPTY_LIST", "Chưa có câu hỏi nào trong ma trận.", 404);
        }

        return list.stream().map(mq -> {
            MatrixQuestionWithOptionsResponse res = new MatrixQuestionWithOptionsResponse();
            res.setMatrixQuestionId(mq.getMatrixQuestionId());
            res.setMatrixId(mq.getMatrix().getMatrixId());
            res.setMatrixName(mq.getMatrix().getMatrixName());
            res.setQuestionId(mq.getQuestion().getQuestionId());
            res.setQuestionText(mq.getQuestion().getQuestionText());
            res.setMarksAllocated(mq.getMarksAllocated());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MatrixQuestionWithOptionsResponse getMatrixQuestionById(Long id) {
        MatrixQuestion mq = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi trong ma trận.", 404));

        MatrixQuestionWithOptionsResponse res = new MatrixQuestionWithOptionsResponse();
        res.setMatrixQuestionId(mq.getMatrixQuestionId());
        res.setMatrixId(mq.getMatrix().getMatrixId());
        res.setMatrixName(mq.getMatrix().getMatrixName());
        res.setQuestionId(mq.getQuestion().getQuestionId());
        res.setQuestionText(mq.getQuestion().getQuestionText());
        res.setMarksAllocated(mq.getMarksAllocated());

        List<OptionsResponse> options = optionsRepository
                .findByQuestion_QuestionId(mq.getQuestion().getQuestionId())
                .stream()
                .map(o -> {
                    OptionsResponse opt = new OptionsResponse();
                    opt.setOptionId(o.getOptionId());
                    opt.setQuestionId(o.getQuestion().getQuestionId());
                    opt.setOptionText(o.getOptionText());
                    opt.setIsCorrect(o.getIsCorrect());
                    opt.setOptionOrder(o.getOptionOrder());
                    opt.setCreatedAt(o.getCreatedAt());
                    opt.setUpdatedAt(o.getUpdatedAt());
                    return opt;
                }).collect(Collectors.toList());

        res.setOptions(options);
        return res;
    }

    @Override
    public MatrixQuestionWithOptionsResponse updateMatrixQuestion(Long id, MatrixQuestionRequest request) {
        MatrixQuestion matrixQuestion = matrixQuestionRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi trong ma trận.", 404));

        Matrix targetMatrix = matrixQuestion.getMatrix(); // mặc định giữ nguyên matrix hiện tại
        Questions targetQuestion = matrixQuestion.getQuestion(); // mặc định giữ nguyên question hiện tại

        // ✅ Nếu có truyền matrixId mới → cập nhật matrix
        if (request.getMatrixId() != null) {
            targetMatrix = matrixRepository.findById(request.getMatrixId())
                    .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));
        }

        // ✅ Nếu có truyền questionIds mới → cập nhật question
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            Long qId = request.getQuestionIds().get(0);
            targetQuestion = questionsRepository.findById(qId)
                    .orElseThrow(() -> new ApiException("QUESTION_NOT_FOUND", "Không tìm thấy câu hỏi ID=" + qId, 404));
        }

        // ✅ Kiểm tra trùng lặp: question đó đã tồn tại trong matrix khác chưa
        if (matrixQuestionRepository.existsByMatrixAndQuestion(targetMatrix, targetQuestion)
                && !matrixQuestion.getQuestion().getQuestionId().equals(targetQuestion.getQuestionId())) {
            throw new ApiException("DUPLICATE_QUESTION",
                    "Câu hỏi này đã tồn tại trong ma trận được chọn.", 400);
        }

        // ✅ Xử lý điểm
        BigDecimal marks = request.getMarksAllocated();
        if (marks == null || marks.compareTo(BigDecimal.ZERO) <= 0) {
            Level level = targetQuestion.getLevel();
            if (level == null) {
                throw new ApiException("LEVEL_NOT_FOUND",
                        "Câu hỏi ID=" + targetQuestion.getQuestionId() + " chưa được gán cấp độ.", 400);
            }
            marks = BigDecimal.valueOf(level.getDifficultyScore());
        }

        // ✅ Cập nhật entity
        matrixQuestion.setMatrix(targetMatrix);
        matrixQuestion.setQuestion(targetQuestion);
        matrixQuestion.setMarksAllocated(marks);

        matrixQuestionRepository.save(matrixQuestion);

        // ✅ Build response
        MatrixQuestionWithOptionsResponse res = new MatrixQuestionWithOptionsResponse();
        res.setMatrixQuestionId(matrixQuestion.getMatrixQuestionId());
        res.setMatrixId(targetMatrix.getMatrixId());
        res.setMatrixName(targetMatrix.getMatrixName());
        res.setQuestionId(targetQuestion.getQuestionId());
        res.setQuestionText(targetQuestion.getQuestionText());
        res.setMarksAllocated(marks);

        List<OptionsResponse> options = optionsRepository
                .findByQuestion_QuestionId(targetQuestion.getQuestionId())
                .stream()
                .map(o -> {
                    OptionsResponse opt = new OptionsResponse();
                    opt.setOptionId(o.getOptionId());
                    opt.setQuestionId(o.getQuestion().getQuestionId());
                    opt.setOptionText(o.getOptionText());
                    opt.setIsCorrect(o.getIsCorrect());
                    opt.setOptionOrder(o.getOptionOrder());
                    opt.setCreatedAt(o.getCreatedAt());
                    opt.setUpdatedAt(o.getUpdatedAt());
                    return opt;
                }).collect(Collectors.toList());

        res.setOptions(options);
        return res;
    }

}
