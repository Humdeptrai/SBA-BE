package sum25.studentcode.backend.modules.StudentPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentPracticeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.service.StudentPracticeService;

import java.util.List;

@RestController
@RequestMapping("/api/student-practices")
@RequiredArgsConstructor
public class StudentPracticeController {

    private final StudentPracticeService studentPracticeService;

    @GetMapping("/{id}")
    public StudentPracticeResponse getStudentPracticeById(@PathVariable Long id) {
        return studentPracticeService.getStudentPracticeById(id);
    }

    @GetMapping
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeService.getAllStudentPractices();
    }

    /** 🧠 Học sinh nộp bài */
    @PutMapping("/{practiceId}/submit")
    public ResponseEntity<StudentPracticeResponse> submitPractice(@PathVariable Long practiceId) {
        return ResponseEntity.ok(studentPracticeService.submitPractice(practiceId));
    }

    /** 👩‍🏫 Giáo viên chấm điểm */
    @PutMapping("/{practiceId}/grade")
    public ResponseEntity<StudentPracticeResponse> gradePractice(
            @PathVariable Long practiceId,
            @RequestBody TeacherGradeRequest request
    ) {
        return ResponseEntity.ok(studentPracticeService.gradePractice(practiceId, request));
    }

    @DeleteMapping("/{id}")
    public void deleteStudentPractice(@PathVariable Long id) {
        studentPracticeService.deleteStudentPractice(id);
    }

    @PostMapping("/enroll")
    public StudentEnrollResponse enrollStudent(@RequestBody StudentEnrollRequest request) {
        return studentPracticeService.enrollStudent(request);
    }


}