package sum25.studentcode.backend.modules.PracticeSession.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final PracticeSessionRepository practiceSessionRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    @Override
    public PracticeSessionResponse createPracticeSession(PracticeSessionRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        PracticeSession practiceSession = PracticeSession.builder()
                .exam(exam)
                .student(student)
                .sessionCode(request.getSessionCode())
                .teacher(teacher)
                .sessionName(request.getSessionName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(request.getIsActive())
                .maxParticipants(request.getMaxParticipants())
                .build();
        practiceSession = practiceSessionRepository.save(practiceSession);
        return convertToResponse(practiceSession);
    }

    @Override
    public PracticeSessionResponse getPracticeSessionById(Long id) {
        PracticeSession practiceSession = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PracticeSession not found"));
        return convertToResponse(practiceSession);
    }

    @Override
    public List<PracticeSessionResponse> getAllPracticeSessions() {
        return practiceSessionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PracticeSessionResponse updatePracticeSession(Long id, PracticeSessionRequest request) {
        PracticeSession practiceSession = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PracticeSession not found"));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        practiceSession.setExam(exam);
        practiceSession.setStudent(student);
        practiceSession.setSessionCode(request.getSessionCode());
        practiceSession.setTeacher(teacher);
        practiceSession.setSessionName(request.getSessionName());
        practiceSession.setStartTime(request.getStartTime());
        practiceSession.setEndTime(request.getEndTime());
        practiceSession.setIsActive(request.getIsActive());
        practiceSession.setMaxParticipants(request.getMaxParticipants());
        practiceSession = practiceSessionRepository.save(practiceSession);
        return convertToResponse(practiceSession);
    }

    @Override
    public void deletePracticeSession(Long id) {
        if (!practiceSessionRepository.existsById(id)) {
            throw new RuntimeException("PracticeSession not found");
        }
        practiceSessionRepository.deleteById(id);
    }

    private PracticeSessionResponse convertToResponse(PracticeSession practiceSession) {
        PracticeSessionResponse response = new PracticeSessionResponse();
        response.setSessionId(practiceSession.getSessionId());
        response.setExamId(practiceSession.getExam().getExamId());
        response.setStudentId(practiceSession.getStudent().getUserId());
        response.setSessionCode(practiceSession.getSessionCode());
        response.setTeacherId(practiceSession.getTeacher().getUserId());
        response.setSessionName(practiceSession.getSessionName());
        response.setStartTime(practiceSession.getStartTime());
        response.setEndTime(practiceSession.getEndTime());
        response.setIsActive(practiceSession.getIsActive());
        response.setMaxParticipants(practiceSession.getMaxParticipants());
        response.setCreatedAt(practiceSession.getCreatedAt());
        response.setUpdatedAt(practiceSession.getUpdatedAt());
        return response;
    }
}