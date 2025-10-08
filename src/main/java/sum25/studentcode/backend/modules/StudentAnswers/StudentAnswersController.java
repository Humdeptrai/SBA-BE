package sum25.studentcode.backend.modules.StudentAnswers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.StudentAnswers.dto.request.StudentAnswersRequest;
import sum25.studentcode.backend.modules.StudentAnswers.dto.response.StudentAnswersResponse;
import sum25.studentcode.backend.modules.StudentAnswers.service.StudentAnswersService;

import java.util.List;

@RestController
@RequestMapping("/api/student-answers")
@RequiredArgsConstructor
public class StudentAnswersController {

    private final StudentAnswersService studentAnswersService;

    @PostMapping
    public StudentAnswersResponse createStudentAnswer(@RequestBody StudentAnswersRequest request) {
        return studentAnswersService.createStudentAnswer(request);
    }

    @GetMapping("/{id}")
    public StudentAnswersResponse getStudentAnswerById(@PathVariable Long id) {
        return studentAnswersService.getStudentAnswerById(id);
    }

    @GetMapping
    public List<StudentAnswersResponse> getAllStudentAnswers() {
        return studentAnswersService.getAllStudentAnswers();
    }

    @PutMapping("/{id}")
    public StudentAnswersResponse updateStudentAnswer(@PathVariable Long id, @RequestBody StudentAnswersRequest request) {
        return studentAnswersService.updateStudentAnswer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteStudentAnswer(@PathVariable Long id) {
        studentAnswersService.deleteStudentAnswer(id);
    }
}