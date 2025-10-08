package sum25.studentcode.backend.modules.StudentPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentPracticeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.service.StudentPracticeService;

import java.util.List;

@RestController
@RequestMapping("/api/student-practices")
@RequiredArgsConstructor
public class StudentPracticeController {

    private final StudentPracticeService studentPracticeService;

    @PostMapping
    public StudentPracticeResponse createStudentPractice(@RequestBody StudentPracticeRequest request) {
        return studentPracticeService.createStudentPractice(request);
    }

    @GetMapping("/{id}")
    public StudentPracticeResponse getStudentPracticeById(@PathVariable Long id) {
        return studentPracticeService.getStudentPracticeById(id);
    }

    @GetMapping
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeService.getAllStudentPractices();
    }

    @PutMapping("/{id}")
    public StudentPracticeResponse updateStudentPractice(@PathVariable Long id, @RequestBody StudentPracticeRequest request) {
        return studentPracticeService.updateStudentPractice(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteStudentPractice(@PathVariable Long id) {
        studentPracticeService.deleteStudentPractice(id);
    }
}