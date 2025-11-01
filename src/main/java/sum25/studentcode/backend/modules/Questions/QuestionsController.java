package sum25.studentcode.backend.modules.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('TEACHER')")
    public QuestionsResponse createQuestion(@RequestBody QuestionsRequest request) {
        return questionsService.createQuestion(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public QuestionsResponse getQuestionById(@PathVariable Long id) {
        return questionsService.getQuestionById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<QuestionsResponse> getAllQuestions() {
        return questionsService.getAllQuestions();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public QuestionsResponse updateQuestion(@PathVariable Long id, @RequestBody QuestionsRequest request) {
        return questionsService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteQuestion(@PathVariable Long id) {
        questionsService.deleteQuestion(id);
    }

    // ✅ Lấy tất cả đáp án (options) của 1 câu hỏi
    @GetMapping("/{questionId}/options")
    @PreAuthorize("hasRole('TEACHER')")
    public List<OptionsResponse> getOptionsByQuestionId(@PathVariable Long questionId) {
        return questionsService.getOptionsByQuestionId(questionId);
    }
    // ✅ Lấy tất cả câu hỏi theo lessonId
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public List<QuestionsResponse> getQuestionsByLessonId(@PathVariable Long lessonId) {
        return questionsService.getQuestionsByLessonId(lessonId);
    }

}
