package sum25.studentcode.backend.modules.StudentPractice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.PracticeQuestionResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentRankingResponse;
import sum25.studentcode.backend.modules.StudentPractice.service.StudentPracticeService;

import java.util.List;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentAnswerDetailResponse;

@RestController
@RequestMapping("/api/student-practices")
@RequiredArgsConstructor
public class StudentPracticeController {

    private final StudentPracticeService studentPracticeService;
    private final UserService userService;;

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

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<StudentPracticeResponse> getAllByStudents() {
        return studentPracticeService.getStudentPracticeRecords();
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

    @GetMapping("/{practiceId}/questions")
    @PreAuthorize("hasRole('STUDENT')")
    public List<PracticeQuestionResponse> getQuestionsForPractice(
            @PathVariable Long practiceId
    ) {
        return studentPracticeService.getQuestionsForPractice(practiceId);
    }

    @GetMapping("/rankings")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<StudentRankingResponse> getRankings(@RequestParam(defaultValue = "desc") String order) {
        return studentPracticeService.getRankings(order);
    }


    @GetMapping("/student/records")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<StudentPracticeResponse> getStudentRecord() {
        return studentPracticeService.getStudentPracticeRecords();
    }

    @GetMapping("/answers")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<StudentAnswerDetailResponse> getStudentAnswersDetails(@RequestParam(required = false) String sessonCode) {
        return studentPracticeService.getStudentAnswersDetails(sessonCode);
    }
}