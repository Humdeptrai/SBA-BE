package sum25.studentcode.backend.modules.MatrixQuestion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionWithOptionsResponse;
import sum25.studentcode.backend.modules.MatrixQuestion.service.MatrixQuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/matrix-questions")
@RequiredArgsConstructor
public class MatrixQuestionController {

    private final MatrixQuestionService matrixQuestionService;

    /** ✅ Thêm câu hỏi vào matrix — trả về luôn options */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<MatrixQuestionWithOptionsResponse> addQuestionsToMatrix(@Valid @RequestBody MatrixQuestionRequest request) {
        return matrixQuestionService.addQuestionsToMatrix(request);
    }

    /** ✅ Lấy chi tiết 1 câu hỏi trong ma trận (kèm options) */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public MatrixQuestionWithOptionsResponse getMatrixQuestionById(@PathVariable Long id) {
        return matrixQuestionService.getMatrixQuestionById(id);
    }

    /** ✅ Lấy tất cả câu hỏi trong toàn hệ thống (kèm options) */
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<MatrixQuestionWithOptionsResponse> getAllMatrixQuestions() {
        return matrixQuestionService.getAllMatrixQuestions();
    }

    /** ✅ Cập nhật câu hỏi trong ma trận */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public MatrixQuestionWithOptionsResponse updateMatrixQuestion(@Valid @PathVariable Long id, @RequestBody MatrixQuestionRequest request) {
        return matrixQuestionService.updateMatrixQuestion(id, request);
    }

    /** ✅ Xóa 1 câu hỏi khỏi ma trận */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteMatrixQuestion(@PathVariable Long id) {
        matrixQuestionService.deleteMatrixQuestion(id);
    }

    /** ✅ Lấy tất cả câu hỏi trong 1 ma trận cụ thể kèm options */
    @GetMapping("/by-matrix/{matrixId}/with-options")
    @PreAuthorize("hasRole('TEACHER')")
    public List<MatrixQuestionWithOptionsResponse> getQuestionsWithOptionsByMatrix(
            @PathVariable Long matrixId
    ) {
        return matrixQuestionService.getQuestionsWithOptionsByMatrixId(matrixId);
    }
}
