package sum25.studentcode.backend.modules.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Questions.dto.request.QuestionsRequest;
import sum25.studentcode.backend.modules.Questions.dto.response.QuestionsResponse;
import sum25.studentcode.backend.modules.Questions.service.QuestionsService;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionsController {

    private final QuestionsService questionsService;

    @PostMapping
    public QuestionsResponse createQuestion(@RequestBody QuestionsRequest request) {
        return questionsService.createQuestion(request);
    }

    @GetMapping("/{id}")
    public QuestionsResponse getQuestionById(@PathVariable Long id) {
        return questionsService.getQuestionById(id);
    }

    @GetMapping
    public List<QuestionsResponse> getAllQuestions() {
        return questionsService.getAllQuestions();
    }

    @PutMapping("/{id}")
    public QuestionsResponse updateQuestion(@PathVariable Long id, @RequestBody QuestionsRequest request) {
        return questionsService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionsService.deleteQuestion(id);
    }

    // ✅ Lấy tất cả đáp án (options) của 1 câu hỏi
    @GetMapping("/{questionId}/options")
    public List<OptionsResponse> getOptionsByQuestionId(@PathVariable Long questionId) {
        return questionsService.getOptionsByQuestionId(questionId);
    }
}
