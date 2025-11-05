package sum25.studentcode.backend.modules.StudentPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.PracticeQuestionResponse;
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
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public StudentPracticeResponse getStudentPracticeById(@PathVariable Long id) {
        return studentPracticeService.getStudentPracticeById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeService.getAllStudentPractices();
    }

    /** 🧠 Học sinh nộp bài (tự động chấm điểm) */
    @PutMapping("/{practiceId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentPracticeResponse submitPractice(@PathVariable Long practiceId) {
        return studentPracticeService.submitPractice(practiceId);
    }

    /** 👩‍🏫 Giáo viên chấm điểm */
    @PutMapping("/{practiceId}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public StudentPracticeResponse gradePractice(
            @PathVariable Long practiceId,
            @RequestBody TeacherGradeRequest request
    ) {
        return studentPracticeService.gradePractice(practiceId, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteStudentPractice(@PathVariable Long id) {
        studentPracticeService.deleteStudentPractice(id);
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentEnrollResponse enrollStudent(@RequestBody StudentEnrollRequest request) {
        return studentPracticeService.enrollStudent(request);
    }

    /** 🧩 Học sinh xem danh sách câu hỏi trong lượt luyện tập */
    @GetMapping("/{practiceId}/questions")
    @PreAuthorize("hasRole('STUDENT')")
    public List<PracticeQuestionResponse> getQuestionsForPractice(
            @PathVariable Long practiceId
    ) {
        return studentPracticeService.getQuestionsForPractice(practiceId);
    }

}