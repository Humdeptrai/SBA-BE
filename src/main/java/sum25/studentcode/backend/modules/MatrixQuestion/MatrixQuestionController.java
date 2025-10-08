package sum25.studentcode.backend.modules.MatrixQuestion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.request.MatrixQuestionRequest;
import sum25.studentcode.backend.modules.MatrixQuestion.dto.response.MatrixQuestionResponse;
import sum25.studentcode.backend.modules.MatrixQuestion.service.MatrixQuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/matrix-questions")
@RequiredArgsConstructor
public class MatrixQuestionController {

    private final MatrixQuestionService matrixQuestionService;

    @PostMapping
    public MatrixQuestionResponse createMatrixQuestion(@RequestBody MatrixQuestionRequest request) {
        return matrixQuestionService.createMatrixQuestion(request);
    }

    @GetMapping("/{id}")
    public MatrixQuestionResponse getMatrixQuestionById(@PathVariable Long id) {
        return matrixQuestionService.getMatrixQuestionById(id);
    }

    @GetMapping
    public List<MatrixQuestionResponse> getAllMatrixQuestions() {
        return matrixQuestionService.getAllMatrixQuestions();
    }

    @PutMapping("/{id}")
    public MatrixQuestionResponse updateMatrixQuestion(@PathVariable Long id, @RequestBody MatrixQuestionRequest request) {
        return matrixQuestionService.updateMatrixQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMatrixQuestion(@PathVariable Long id) {
        matrixQuestionService.deleteMatrixQuestion(id);
    }
}