package sum25.studentcode.backend.modules.Exam;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Exam.dto.request.ExamRequest;
import sum25.studentcode.backend.modules.Exam.dto.response.ExamResponse;
import sum25.studentcode.backend.modules.Exam.service.ExamService;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ExamResponse createExam(@RequestBody ExamRequest request) {
        return examService.createExam(request);
    }

    @GetMapping("/{id}")
    public ExamResponse getExamById(@PathVariable Long id) {
        return examService.getExamById(id);
    }

    @GetMapping
    public List<ExamResponse> getAllExams() {
        return examService.getAllExams();
    }

    @PutMapping("/{id}")
    public ExamResponse updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        return examService.updateExam(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
    }
}