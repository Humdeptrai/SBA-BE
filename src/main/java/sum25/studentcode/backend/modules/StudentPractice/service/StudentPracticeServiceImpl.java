package sum25.studentcode.backend.modules.StudentPractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentPracticeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentPracticeServiceImpl implements StudentPracticeService {

    private final StudentPracticeRepository studentPracticeRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final UserRepository userRepository;

    @Override
    public StudentPracticeResponse createStudentPractice(StudentPracticeRequest request) {
        PracticeSession practiceSession = practiceSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("PracticeSession not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        StudentPractice studentPractice = StudentPractice.builder()
                .practiceSession(practiceSession)
                .student(student)
                .perTime(request.getPerTime())
                .submitTime(request.getSubmitTime())
                .totalScore(request.getTotalScore())
                .status(request.getStatus())
                .build();
        studentPractice = studentPracticeRepository.save(studentPractice);
        return convertToResponse(studentPractice);
    }

    @Override
    public StudentPracticeResponse getStudentPracticeById(Long id) {
        StudentPractice studentPractice = studentPracticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentPractice not found"));
        return convertToResponse(studentPractice);
    }

    @Override
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentPracticeResponse updateStudentPractice(Long id, StudentPracticeRequest request) {
        StudentPractice studentPractice = studentPracticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentPractice not found"));
        PracticeSession practiceSession = practiceSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("PracticeSession not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        studentPractice.setPracticeSession(practiceSession);
        studentPractice.setStudent(student);
        studentPractice.setPerTime(request.getPerTime());
        studentPractice.setSubmitTime(request.getSubmitTime());
        studentPractice.setTotalScore(request.getTotalScore());
        studentPractice.setStatus(request.getStatus());
        studentPractice = studentPracticeRepository.save(studentPractice);
        return convertToResponse(studentPractice);
    }

    @Override
    public void deleteStudentPractice(Long id) {
        if (!studentPracticeRepository.existsById(id)) {
            throw new RuntimeException("StudentPractice not found");
        }
        studentPracticeRepository.deleteById(id);
    }

    private StudentPracticeResponse convertToResponse(StudentPractice studentPractice) {
        StudentPracticeResponse response = new StudentPracticeResponse();
        response.setPracticeId(studentPractice.getPracticeId());
        response.setSessionId(studentPractice.getPracticeSession().getSessionId());
        response.setStudentId(studentPractice.getStudent().getUserId());
        response.setPerTime(studentPractice.getPerTime());
        response.setSubmitTime(studentPractice.getSubmitTime());
        response.setTotalScore(studentPractice.getTotalScore());
        response.setStatus(studentPractice.getStatus());
        response.setCreatedAt(studentPractice.getCreatedAt());
        response.setUpdatedAt(studentPractice.getUpdatedAt());
        return response;
    }
}