package sum25.studentcode.backend.modules.QuestionType;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.QuestionType.dto.request.QuestionTypeRequest;
import sum25.studentcode.backend.modules.QuestionType.dto.response.QuestionTypeResponse;
import sum25.studentcode.backend.modules.QuestionType.service.QuestionTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/question-types")
@RequiredArgsConstructor
public class QuestionTypeController {

    private final QuestionTypeService questionTypeService;

    @PostMapping
    public QuestionTypeResponse createQuestionType(@RequestBody QuestionTypeRequest request) {
        return questionTypeService.createQuestionType(request);
    }

    @GetMapping("/{id}")
    public QuestionTypeResponse getQuestionTypeById(@PathVariable Long id) {
        return questionTypeService.getQuestionTypeById(id);
    }

    @GetMapping
    public List<QuestionTypeResponse> getAllQuestionTypes() {
        return questionTypeService.getAllQuestionTypes();
    }

    @PutMapping("/{id}")
    public QuestionTypeResponse updateQuestionType(@PathVariable Long id, @RequestBody QuestionTypeRequest request) {
        return questionTypeService.updateQuestionType(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestionType(@PathVariable Long id) {
        questionTypeService.deleteQuestionType(id);
    }
}