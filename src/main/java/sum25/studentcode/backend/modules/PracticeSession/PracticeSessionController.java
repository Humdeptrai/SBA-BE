package sum25.studentcode.backend.modules.PracticeSession;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.service.PracticeSessionService;

import java.util.List;

@RestController
@RequestMapping("/api/practice-sessions")
@RequiredArgsConstructor
public class PracticeSessionController {

    private final PracticeSessionService practiceSessionService;

    @PostMapping
    public PracticeSessionResponse createPracticeSession(@RequestBody PracticeSessionRequest request) {
        return practiceSessionService.createPracticeSession(request);
    }

    @GetMapping("/{id}")
    public PracticeSessionResponse getPracticeSessionById(@PathVariable Long id) {
        return practiceSessionService.getPracticeSessionById(id);
    }

    @GetMapping
    public List<PracticeSessionResponse> getAllPracticeSessions() {
        return practiceSessionService.getAllPracticeSessions();
    }

    @PutMapping("/{id}")
    public PracticeSessionResponse updatePracticeSession(@PathVariable Long id, @RequestBody PracticeSessionRequest request) {
        return practiceSessionService.updatePracticeSession(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePracticeSession(@PathVariable Long id) {
        practiceSessionService.deletePracticeSession(id);
    }

    @GetMapping("/lesson/{lessonId}")
    public List<PracticeSessionResponse> getPracticeSessionsByLessonId(@PathVariable Long lessonId) {
        return practiceSessionService.getPracticeSessionsByLessonId(lessonId);
    }

}