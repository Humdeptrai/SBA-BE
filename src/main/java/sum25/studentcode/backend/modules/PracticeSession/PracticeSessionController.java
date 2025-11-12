package sum25.studentcode.backend.modules.PracticeSession;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionStudentResponse;
import sum25.studentcode.backend.modules.PracticeSession.service.PracticeSessionService;

import java.util.List;

@RestController
@RequestMapping("/api/practice-sessions")
@RequiredArgsConstructor
public class PracticeSessionController {

    private final PracticeSessionService practiceSessionService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public PracticeSessionResponse createPracticeSession(@RequestBody PracticeSessionRequest request) {
        return practiceSessionService.createPracticeSession(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public PracticeSessionResponse getPracticeSessionById(@PathVariable Long id) {
        return practiceSessionService.getPracticeSessionById(id);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<PracticeSessionStudentResponse> getAllPracticeSessions() {
        return practiceSessionService.getAllPracticeSessionsForStudents();
    }

    @GetMapping("by/user/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<PracticeSessionResponse> getAllPracticeSessions(@PathVariable Long userId) {
        return practiceSessionService.getAllPracticeSessions(userId);
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public List<PracticeSessionStudentResponse> getAllPracticeSessionsForStudents() {
        return practiceSessionService.getAllPracticeSessionsForStudents();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public PracticeSessionResponse updatePracticeSession(@PathVariable Long id, @RequestBody PracticeSessionRequest request) {
        return practiceSessionService.updatePracticeSession(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deletePracticeSession(@PathVariable Long id) {
        practiceSessionService.deletePracticeSession(id);
    }

//    @GetMapping("/enrolled")
//    @PreAuthorize("hasRole('STUDENT')")
//    public List<Long> getEnrolledSessionIds() {
//        return practiceSessionService.getEnrolledSessionIdsForStudent();
//    }

//    @GetMapping("/lesson/{lessonId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
//    public List<PracticeSessionResponse> getPracticeSessionsByLessonId(@PathVariable Long lessonId) {
//        return practiceSessionService.getPracticeSessionsByLessonId(lessonId);
//    }

}