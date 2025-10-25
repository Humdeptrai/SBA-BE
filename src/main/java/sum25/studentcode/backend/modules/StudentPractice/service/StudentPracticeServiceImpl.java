package sum25.studentcode.backend.modules.StudentPractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.PracticeQuestionResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.model.StudentAnswers;

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
    private final MatrixQuestionRepository matrixQuestionRepository;
    private final OptionsRepository optionsRepository;
    private final StudentAnswersRepository studentAnswersRepository;



    @Override
    public StudentPracticeResponse getStudentPracticeById(Long id) {
        StudentPractice studentPractice = studentPracticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentPractice not found"));
        return convertToResponse(studentPractice);
    }

    @Override
    public List<PracticeQuestionResponse> getQuestionsForPractice(Long practiceId) {
        // 1. Lấy practice + user
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy user trong hệ thống.", 404));

        if (!practice.getStudent().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền truy cập bài luyện tập này.", 403);
        }

        // 2. Kiểm tra session
        PracticeSession session = practice.getPracticeSession();
        if (session == null)
            throw new ApiException("SESSION_NOT_FOUND", "Buổi luyện tập không tồn tại.", 404);
        if (!session.getIsActive())
            throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập không hoạt động.", 400);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime()))
            throw new ApiException("SESSION_TIME_INVALID", "Hiện không trong thời gian làm bài.", 400);

        // 3. Kiểm tra trạng thái practice
        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS)
            throw new ApiException("INVALID_STATUS", "Chỉ xem đề khi đang làm bài (IN_PROGRESS).", 400);

        // 4. Lấy câu hỏi từ Matrix
        Matrix matrix = session.getMatrix();
        if (matrix == null)
            throw new ApiException("MATRIX_NOT_FOUND", "Buổi luyện tập chưa gắn ma trận đề thi.", 404);

        // (Optional) Cập nhật thời gian bắt đầu làm
        if (practice.getPerTime() == null) {
            practice.setPerTime(LocalDateTime.now());
            studentPracticeRepository.save(practice);
        }

        // 5. Trả câu hỏi như trước
        List<MatrixQuestion> mqs = matrixQuestionRepository.findByMatrix_MatrixId(matrix.getMatrixId());
        if (mqs.isEmpty())
            throw new ApiException("MATRIX_EMPTY", "Đề thi chưa có câu hỏi.", 404);

        return mqs.stream().map(mq -> {
            Questions q = mq.getQuestion();
            PracticeQuestionResponse dto = new PracticeQuestionResponse();
            dto.setQuestionId(q.getQuestionId());
            dto.setQuestionText(q.getQuestionText());
            dto.setOptions(optionsRepository.findByQuestion_QuestionId(q.getQuestionId())
                    .stream()
                    .map(o -> {
                        PracticeQuestionResponse.OptionItem opt = new PracticeQuestionResponse.OptionItem();
                        opt.setOptionId(o.getOptionId());
                        opt.setOptionText(o.getOptionText());
                        opt.setOptionOrder(o.getOptionOrder());
                        return opt;
                    })
                    .toList());
            return dto;
        }).toList();
    }


    @Override
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public StudentPracticeResponse submitPractice(Long practiceId) {
        // 🔹 Lấy thông tin practice
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

        // 🔹 Lấy user hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy user trong hệ thống.", 404));

        // 🔹 Check quyền: chỉ student sở hữu practice mới được submit
        if (!practice.getStudent().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền nộp bài này.", 403);
        }

        // 🔹 Kiểm tra session còn hoạt động không
        PracticeSession session = practice.getPracticeSession();
        if (session == null)
            throw new ApiException("SESSION_NOT_FOUND", "Buổi luyện tập không tồn tại.", 404);
        if (!session.getIsActive())
            throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập này đã bị khóa hoặc không còn hoạt động.", 400);

        // 🔹 Kiểm tra thời gian hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime()))
            throw new ApiException("SESSION_TIME_INVALID", "Không thể nộp bài ngoài thời gian làm bài.", 400);

        // 🔹 Kiểm tra trạng thái
        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS) {
            throw new ApiException("INVALID_STATUS", "Chỉ có thể nộp bài khi đang ở trạng thái IN_PROGRESS.", 400);
        }

        // ✅ Bổ sung: Nạp danh sách câu trả lời trước khi tính điểm
        List<StudentAnswers> answers = studentAnswersRepository.findByStudentPractice_PracticeId(practiceId);
        practice.setStudentAnswers(answers);

        if (answers == null || answers.isEmpty()) {
            throw new ApiException("NO_ANSWERS", "Chưa có câu trả lời nào được nộp.", 400);
        }

        // ✅ Tính tổng điểm
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

    public BigDecimal calculateTotalScore(StudentPractice practice) {
        if (practice.getStudentAnswers() == null || practice.getStudentAnswers().isEmpty()) {
            System.out.println("⚠️ Không có câu trả lời nào trong student_answers");
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = BigDecimal.ZERO;

        // Gom nhóm theo câu hỏi
        var answersByQuestion = practice.getStudentAnswers()
                .stream()
                .collect(Collectors.groupingBy(sa -> sa.getQuestion().getQuestionId()));

        for (var entry : answersByQuestion.entrySet()) {
            Long questionId = entry.getKey();
            List<StudentAnswers> answers = entry.getValue();
            Questions question = answers.get(0).getQuestion();

            BigDecimal marksPerQuestion = matrixQuestionRepository
                    .findByQuestion_QuestionId(questionId)
                    .stream()
                    .findFirst()
                    .map(MatrixQuestion::getMarksAllocated)
                    .orElse(BigDecimal.ONE);

            List<Long> correctOptionIds = optionsRepository.findByQuestion_QuestionId(questionId)
                    .stream()
                    .filter(Options::getIsCorrect)
                    .map(Options::getOptionId)
                    .toList();

            System.out.println("\n=== DEBUG QUESTION ID: " + questionId + " ===");
            System.out.println("Điểm câu hỏi: " + marksPerQuestion);
            System.out.println("Đáp án đúng: " + correctOptionIds);

            long correctChosen = answers.stream()
                    .filter(a -> {
                        Long selected = a.getSelectedOptionId();
                        boolean match = correctOptionIds.contains(Long.valueOf(selected));
                        System.out.println(" - Chọn: " + selected + " | match=" + match);
                        return match;
                    })
                    .count();

            double ratio = (double) correctChosen / correctOptionIds.size();
            BigDecimal partialScore = marksPerQuestion.multiply(BigDecimal.valueOf(ratio));
            System.out.println("✅ Số đúng: " + correctChosen + "/" + correctOptionIds.size() +
                    " | ratio=" + ratio + " | điểm cộng=" + partialScore);

            answers.forEach(a -> a.setMarksEarned(partialScore));
            totalScore = totalScore.add(partialScore);
        }

        System.out.println("🎯 Tổng điểm cuối cùng = " + totalScore);
        return totalScore;
    }


    @Override
    public StudentEnrollResponse enrollStudent(StudentEnrollRequest request) {
        // ✅ Lấy user hiện đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("DEBUG username from token = " + username);
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

        // ✅ Lấy thông tin matrix và exam
        var matrix = session.getMatrix();
        if (matrix == null) {
            throw new RuntimeException("This session is not linked to any matrix.");
        }

        var exam = matrix.getExam(); // có thể null nếu matrix chưa gắn exam

        // ✅ Tạo bản ghi StudentPractice
        StudentPractice practice = StudentPractice.builder()
                .practiceSession(session)
                .student(student)
                .examCode(exam != null ? exam.getExamCode() : null)
                .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                .perTime(LocalDateTime.now())
                .build();

        studentPracticeRepository.save(practice);

        // ✅ Trả về thông tin session
        StudentEnrollResponse response = new StudentEnrollResponse();
        response.setPracticeId(practice.getPracticeId());
        response.setSessionId(session.getSessionId());
        response.setSessionName(session.getSessionName());
        response.setExamName(exam != null ? exam.getExamName() : "(Matrix chưa gắn Exam)");
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
