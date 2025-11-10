package sum25.studentcode.backend.modules.PracticeSession.service;

import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionStudentResponse;

import java.util.List;

public interface PracticeSessionService {
    PracticeSessionResponse createPracticeSession(PracticeSessionRequest request);
    PracticeSessionResponse getPracticeSessionById(Long id);
    List<PracticeSessionResponse> getAllPracticeSessions(Long userId);
    List<PracticeSessionStudentResponse> getAllPracticeSessionsForStudents();
    PracticeSessionResponse updatePracticeSession(Long id, PracticeSessionRequest request);
    void deletePracticeSession(Long id);
}
