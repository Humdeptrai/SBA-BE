package sum25.studentcode.backend.modules.PracticeSession;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<PracticeSessionResponse> getAllPracticeSessions() {
        return practiceSessionService.getAllPracticeSessions();
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

//    @GetMapping("/lesson/{lessonId}")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
//    public List<PracticeSessionResponse> getPracticeSessionsByLessonId(@PathVariable Long lessonId) {
//        return practiceSessionService.getPracticeSessionsByLessonId(lessonId);
//    }

}