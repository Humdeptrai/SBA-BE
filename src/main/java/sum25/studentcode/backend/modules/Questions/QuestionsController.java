package sum25.studentcode.backend.modules.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/by/user/{userId}")
    @PreAuthorize("hasRole('TEACHER')")
    public List<QuestionsResponse> getAllQuestions(@RequestParam(value = "page", defaultValue = "0") int page
            , @RequestParam(value = "size", defaultValue = "10") int size, @PathVariable Long userId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return questionsService.getAllQuestions(pageable, userId);
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

    @GetMapping("/{questionId}/options")
    @PreAuthorize("hasRole('TEACHER')")
    public List<OptionsResponse> getOptionsByQuestionId(@PathVariable Long questionId) {
        return questionsService.getOptionsByQuestionId(questionId);
    }
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public List<QuestionsResponse> getQuestionsByLessonId(@PathVariable Long lessonId) {
        return questionsService.getQuestionsByLessonId(lessonId);
    }

    @GetMapping("/level")
    @PreAuthorize("hasRole('TEACHER')")
    public Page<QuestionsResponse> getQuestionForMatrixWithUniqueByLevelName(@RequestParam String levelName,
                                                                             @RequestParam Long lessonId,
                                                                             @RequestParam int page,
                                                                             @RequestParam int size) {
        return questionsService.getQuestionForMatrixWithUniqueByLevelName(levelName,lessonId, page, size);
    }

}
