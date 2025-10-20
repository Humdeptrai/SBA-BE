package sum25.studentcode.backend.modules.StudentPractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentPracticeServiceImpl implements StudentPracticeService {

    private final StudentPracticeRepository studentPracticeRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final UserRepository userRepository;


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

    /** 🧠 Học sinh nộp bài */
    @Transactional
    @Override
    public StudentPracticeResponse submitPractice(Long practiceId) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot submit unless status is IN_PROGRESS");
        }

        // ✅ Tự tính tổng điểm khi nộp
        BigDecimal totalScore = calculateTotalScore(practice);
        practice.setTotalScore(totalScore);
        practice.setSubmitTime(LocalDateTime.now());
        practice.setStatus(StudentPractice.PracticeStatus.SUBMITTED);

        studentPracticeRepository.save(practice);
        return convertToResponse(practice);
    }


    /** 👩‍🏫 Giáo viên chấm điểm */
    @Override
    public StudentPracticeResponse gradePractice(Long practiceId, TeacherGradeRequest request) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        if (practice.getStatus() != StudentPractice.PracticeStatus.SUBMITTED) {
            throw new RuntimeException("Cannot grade unless practice is SUBMITTED");
        }

        practice.setTotalScore(request.getTotalScore());
        practice.setStatus(StudentPractice.PracticeStatus.GRADED);
        practice.setUpdatedAt(LocalDateTime.now());
        studentPracticeRepository.save(practice);

        return convertToResponse(practice);
    }


    @Override
    public void deleteStudentPractice(Long id) {
        if (!studentPracticeRepository.existsById(id)) {
            throw new RuntimeException("StudentPractice not found");
        }
        studentPracticeRepository.deleteById(id);
    }

    private BigDecimal calculateTotalScore(StudentPractice practice) {
        if (practice.getStudentAnswers() == null || practice.getStudentAnswers().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return practice.getStudentAnswers().stream()
                .map(a -> a.getMarksEarned() != null ? a.getMarksEarned() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public StudentEnrollResponse enrollStudent(StudentEnrollRequest request) {
        // ✅ Lấy user hiện đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated student not found"));

        // ✅ Lấy session
        PracticeSession session = practiceSessionRepository.findBySessionCode(request.getSessionCode())
                .orElseThrow(() -> new RuntimeException("Session not found or invalid code"));

        if (!session.getIsActive()) {
            throw new RuntimeException("This practice session is not active.");
        }

        // ✅ Kiểm tra nếu đã đăng ký buổi này
        boolean exists = studentPracticeRepository.existsByPracticeSessionAndStudent(session, student);
        if (exists) {
            throw new RuntimeException("Student already enrolled in this session.");
        }

        // ✅ Tạo bản ghi StudentPractice
        StudentPractice practice = StudentPractice.builder()
                .practiceSession(session)
                .student(student)
                .examCode(session.getExam().getExamCode())
                .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                .perTime(LocalDateTime.now())
                .build();

        studentPracticeRepository.save(practice);

        // ✅ Trả về thông tin session
        StudentEnrollResponse response = new StudentEnrollResponse();
        response.setPracticeId(practice.getPracticeId());
        response.setSessionId(session.getSessionId());
        response.setSessionName(session.getSessionName());
        response.setExamName(session.getExam().getExamName());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setStatus(practice.getStatus().name());
        return response;
    }


    private StudentPracticeResponse convertToResponse(StudentPractice studentPractice) {
        StudentPracticeResponse response = new StudentPracticeResponse();
        response.setPracticeId(studentPractice.getPracticeId());
        response.setSessionId(studentPractice.getPracticeSession().getSessionId());
        response.setStudentId(studentPractice.getStudent().getUserId());
        response.setPerTime(studentPractice.getPerTime());
        response.setSubmitTime(studentPractice.getSubmitTime());
        response.setTotalScore(studentPractice.getTotalScore());
        response.setStatus(studentPractice.getStatus().name()); // ✅ Enum → String
        response.setCreatedAt(studentPractice.getCreatedAt());
        response.setUpdatedAt(studentPractice.getUpdatedAt());
        return response;
    }
}
