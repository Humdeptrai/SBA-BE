package sum25.studentcode.backend.modules.StudentPractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.PracticeQuestionResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentEnrollResponse;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));
        return convertToResponse(studentPractice);
    }

    @Override
    public List<StudentPracticeResponse> getAllStudentPractices() {
        return studentPracticeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteStudentPractice(Long id) {
        if (!studentPracticeRepository.existsById(id)) {
            throw new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404);
        }
        studentPracticeRepository.deleteById(id);
    }


    @Override
    public List<PracticeQuestionResponse> getQuestionsForPractice(Long practiceId) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

        // 🔐 Check user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy user trong hệ thống.", 404));

        if (!practice.getStudent().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền truy cập bài luyện tập này.", 403);
        }

        PracticeSession session = practice.getPracticeSession();
        if (session == null) throw new ApiException("SESSION_NOT_FOUND", "Buổi luyện tập không tồn tại.", 404);
        if (!session.getIsActive()) throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập không hoạt động.", 400);

        // ✅ Kiểm tra thời gian hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (session.getExamDate() == null || session.getDurationMinutes() == null)
            throw new ApiException("SESSION_TIME_INVALID", "Buổi luyện tập chưa cấu hình thời gian.", 500);

        LocalDateTime start = session.getExamDate();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());
        if (now.isBefore(start) || now.isAfter(end))
            throw new ApiException("SESSION_TIME_INVALID", "Hiện không trong thời gian làm bài.", 400);

        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS)
            throw new ApiException("INVALID_STATUS", "Chỉ xem đề khi đang làm bài (IN_PROGRESS).", 400);

        // ✅ Ghi nhận thời gian bắt đầu làm
        if (practice.getPerTime() == null) {
            practice.setPerTime(LocalDateTime.now());
            studentPracticeRepository.save(practice);
        }

        // ✅ Lấy câu hỏi từ matrix
        Matrix matrix = session.getMatrix();
        if (matrix == null)
            throw new ApiException("MATRIX_NOT_FOUND", "Buổi luyện tập chưa gắn ma trận đề.", 500);

        List<MatrixQuestion> mqs = matrixQuestionRepository.findByMatrix_MatrixId(matrix.getMatrixId());
        if (mqs.isEmpty())
            throw new ApiException("MATRIX_EMPTY", "Đề thi chưa có câu hỏi.", 404);

        return mqs.stream().map(mq -> {
            Questions q = mq.getQuestion();
            PracticeQuestionResponse dto = new PracticeQuestionResponse();
            dto.setQuestionId(q.getQuestionId());
            dto.setQuestionText(q.getQuestionText());
            dto.setOptions(
                    optionsRepository.findByQuestion_QuestionId(q.getQuestionId())
                            .stream()
                            .map(o -> new PracticeQuestionResponse.OptionItem(o.getOptionId(), o.getOptionText(), o.getOptionOrder()))
                            .toList()
            );
            return dto;
        }).toList();
    }

    @Transactional
    @Override
    public StudentPracticeResponse submitPractice(Long practiceId) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy user trong hệ thống.", 404));

        if (!practice.getStudent().getUserId().equals(currentUser.getUserId()))
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền nộp bài này.", 403);

        PracticeSession session = practice.getPracticeSession();
        if (session == null)
            throw new ApiException("SESSION_NOT_FOUND", "Buổi luyện tập không tồn tại.", 404);
        if (!session.getIsActive())
            throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập đã bị khóa hoặc không còn hoạt động.", 400);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = session.getExamDate();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());

        if (now.isBefore(start) || now.isAfter(end))
            throw new ApiException("SESSION_TIME_INVALID", "Không thể nộp bài ngoài thời gian làm bài.", 400);

        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS)
            throw new ApiException("INVALID_STATUS", "Chỉ có thể nộp bài khi đang ở trạng thái IN_PROGRESS.", 400);

        // ✅ Load câu trả lời
        List<StudentAnswers> answers = studentAnswersRepository.findLatestAnswersByPracticeId(practiceId);


        // ✅ Giữ lại bản mới nhất của mỗi câu hỏi
        answers = answers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getQuestionId()))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(StudentAnswers::getAnsweredAt))
                        .get())
                .collect(Collectors.toList()); // ✅ mutable list

        // ✅ Gán danh sách sau khi lọc vào practice
        practice.setStudentAnswers(answers);

        // ✅ Tính tổng điểm chính xác
        BigDecimal totalScore = calculateTotalScore(practice);

        practice.setTotalScore(totalScore);
        practice.setSubmitTime(LocalDateTime.now());
        practice.setStatus(StudentPractice.PracticeStatus.SUBMITTED);

        studentPracticeRepository.save(practice);

        System.out.println("✅ [SUBMIT] Practice " + practiceId + " submitted with " + answers.size() + " unique questions.");

        return convertToResponse(practice);
    }



    @Override
    public StudentPracticeResponse gradePractice(Long practiceId, TeacherGradeRequest request) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

        if (practice.getStatus() != StudentPractice.PracticeStatus.SUBMITTED)
            throw new ApiException("INVALID_STATUS", "Chỉ chấm điểm khi bài đã được nộp (SUBMITTED).", 400);

        practice.setTotalScore(request.getTotalScore());
        practice.setStatus(StudentPractice.PracticeStatus.GRADED);
        practice.setUpdatedAt(LocalDateTime.now());
        studentPracticeRepository.save(practice);

        return convertToResponse(practice);
    }

    @Override
    public StudentEnrollResponse enrollStudent(StudentEnrollRequest request) {
        // ✅ Lấy user hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        // ✅ Kiểm tra sessionId hợp lệ
        PracticeSession session = practiceSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập này.", 404));

        // ✅ Kiểm tra sessionCode đúng
        if (!session.getSessionCode().equals(request.getSessionCode())) {
            throw new ApiException("INVALID_SESSION_CODE", "Mã code không khớp với buổi luyện tập.", 400);
        }

        // ✅ Check session hoạt động
        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập này hiện không hoạt động.", 400);
        }

        // ✅ Kiểm tra thời gian hợp lệ - SỬ DỤNG TIMEZONE VN
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(vnZone);
        LocalDateTime start = session.getExamDate();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());

        System.out.println("🕒 [ENROLL] Giờ hiện tại VN: " + now);
        System.out.println("🕒 [ENROLL] Giờ bắt đầu: " + start);
        System.out.println("🕒 [ENROLL] Giờ kết thúc: " + end);

        if (now.isBefore(start)) {
            throw new ApiException("SESSION_NOT_STARTED", "Bài thi chưa đến thời gian bắt đầu.", 400);
        }
        if (now.isAfter(end)) {
            throw new ApiException("SESSION_ENDED", "Bài thi đã kết thúc.", 400);
        }

        // ✅ Check học sinh đã enroll chưa
        Optional<StudentPractice> existingPractice =
                studentPracticeRepository.findByPracticeSessionAndStudent(session, student);

        StudentPractice practice;

        if (existingPractice.isPresent()) {
            // ✅ Đã có rồi → cho phép vào lại (không tạo mới)
            practice = existingPractice.get();

            // ✅ Kiểm tra nếu đã nộp bài rồi thì không cho vào
            if (practice.getStatus() == StudentPractice.PracticeStatus.SUBMITTED) {
                throw new ApiException("ALREADY_SUBMITTED",
                        "Bạn đã nộp bài thi này rồi. Không thể làm lại.", 400);
            }

            System.out.println("✅ [ENROLL] Student re-entering exam. PracticeId: " + practice.getPracticeId());
        } else {
            // ✅ Lần đầu enroll → tạo mới
            practice = StudentPractice.builder()
                    .practiceSession(session)
                    .student(student)
                    .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                    .perTime(now) // Lưu thời điểm bắt đầu làm
                    .build();

            studentPracticeRepository.save(practice);

            // ✅ Cập nhật số lượng tham gia (chỉ tăng lần đầu)
            if (session.getCurrentParticipants() == null) session.setCurrentParticipants(0);
            session.setCurrentParticipants(session.getCurrentParticipants() + 1);
            practiceSessionRepository.save(session);

            System.out.println("✅ [ENROLL] New student enrolled. PracticeId: " + practice.getPracticeId());
        }

        // ✅ Trả về response
        return StudentEnrollResponse.builder()
                .practiceId(practice.getPracticeId())
                .sessionId(session.getSessionId())
                .sessionName(session.getSessionName())
                .startTime(start)
                .endTime(end)
                .status(practice.getStatus().name())
                .build();
    }


    private BigDecimal calculateTotalScore(StudentPractice practice) {
        if (practice.getStudentAnswers() == null || practice.getStudentAnswers().isEmpty())
            return BigDecimal.ZERO;

        BigDecimal totalScore = BigDecimal.ZERO;

        for (StudentAnswers answer : practice.getStudentAnswers()) {
            Questions question = answer.getQuestion();
            if (question == null) continue;

            // ✅ Lấy thông tin MatrixQuestion để biết marksAllocated
            Optional<MatrixQuestion> mqOpt = matrixQuestionRepository.findByQuestion_QuestionId(question.getQuestionId())
                    .stream().findFirst();

            BigDecimal marksAllocated = mqOpt.map(MatrixQuestion::getMarksAllocated).orElse(BigDecimal.ZERO);

            // ✅ Nếu marksAllocated = 0 → dùng difficultyScore theo level
            BigDecimal baseScore;
            if (marksAllocated.compareTo(BigDecimal.ZERO) == 0) {
                Double diff = question.getLevel() != null ? question.getLevel().getDifficultyScore() : 1.0;
                baseScore = BigDecimal.valueOf(diff);
            } else {
                baseScore = marksAllocated;
            }

            // ✅ Tìm các option đúng
            List<Long> correctIds = optionsRepository.findByQuestion_QuestionId(question.getQuestionId())
                    .stream()
                    .filter(Options::getIsCorrect)
                    .map(Options::getOptionId)
                    .toList();

            boolean isCorrect = correctIds.contains(answer.getSelectedOptionId());
            answer.setIsCorrect(isCorrect);
            answer.setMarksEarned(isCorrect ? baseScore : BigDecimal.ZERO);
            studentAnswersRepository.save(answer);

            if (isCorrect) {
                totalScore = totalScore.add(baseScore);
            }
        }

        return totalScore;
    }


    private StudentPracticeResponse convertToResponse(StudentPractice sp) {
        StudentPracticeResponse res = new StudentPracticeResponse();
        res.setPracticeId(sp.getPracticeId());
        res.setSessionId(sp.getPracticeSession().getSessionId());
        res.setStudentId(sp.getStudent().getUserId());
        res.setPerTime(sp.getPerTime());
        res.setSubmitTime(sp.getSubmitTime());
        res.setTotalScore(sp.getTotalScore());
        res.setStatus(sp.getStatus().name());
        res.setCreatedAt(sp.getCreatedAt());
        res.setUpdatedAt(sp.getUpdatedAt());
        return res;
    }
}
