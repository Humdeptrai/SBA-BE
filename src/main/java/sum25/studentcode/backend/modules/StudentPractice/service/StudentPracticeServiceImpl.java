package sum25.studentcode.backend.modules.StudentPractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Auth.service.IUserService;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Options.repository.OptionsRepository;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.StudentEnrollRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.request.TeacherGradeRequest;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.*;
import sum25.studentcode.backend.modules.StudentPractice.repository.StudentPracticeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    private final IUserService userService;

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


    @Transactional
    @Override
    public List<PracticeQuestionResponse> getQuestionsForPractice(Long practiceId) {
        StudentPractice practice = studentPracticeRepository.findById(practiceId)
                .orElseThrow(() -> new ApiException("PRACTICE_NOT_FOUND", "Không tìm thấy lượt luyện tập.", 404));

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

        LocalDateTime now = LocalDateTime.now();
        if (session.getExamDate() == null || session.getDurationMinutes() == null)
            throw new ApiException("SESSION_TIME_INVALID", "Buổi luyện tập chưa cấu hình thời gian.", 500);

        LocalDateTime start = session.getExamDate();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());
        if (now.isBefore(start) || now.isAfter(end))
            throw new ApiException("SESSION_TIME_INVALID", "Hiện không trong thời gian làm bài.", 400);

        if (practice.getStatus() != StudentPractice.PracticeStatus.IN_PROGRESS)
            throw new ApiException("INVALID_STATUS", "Chỉ xem đề khi đang làm bài (IN_PROGRESS).", 400);

        if (practice.getPerTime() == null) {
            practice.setPerTime(LocalDateTime.now());
            studentPracticeRepository.save(practice);
        }

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

    @Override
    public List<StudentPracticeResponse> getStudentPracticeRecords() {
        User user = userService.getCurrentUser();
        List<StudentPracticeResponse> responses = new ArrayList<>();
        for(StudentPractice studentPractices : studentPracticeRepository.findAllByStudent_UserId(user.getUserId())){
            StudentPracticeResponse studentPracticeResponse = new StudentPracticeResponse();
            studentPracticeResponse.setPracticeId(studentPractices.getPracticeId());
            studentPracticeResponse.setSessionId(studentPractices.getPracticeSession().getSessionId());
            studentPracticeResponse.setStudentId(studentPractices.getStudent().getUserId());
            studentPracticeResponse.setPerTime(studentPractices.getPerTime());
            studentPracticeResponse.setSubmitTime(studentPractices.getSubmitTime());
            studentPracticeResponse.setTotalScore(studentPractices.getTotalScore());
            studentPracticeResponse.setStatus(studentPractices.getStatus().name());
            studentPracticeResponse.setCreatedAt(studentPractices.getCreatedAt());
            studentPracticeResponse.setUpdatedAt(studentPractices.getUpdatedAt());

            studentPracticeResponse.setSessionName(studentPractices.getPracticeSession().getSessionName());
            responses.add(studentPracticeResponse);
        }
        return responses;
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

        List<StudentAnswers> answers = studentAnswersRepository.findLatestAnswersByPracticeId(practiceId);


        answers = answers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getQuestionId()))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(StudentAnswers::getAnsweredAt))
                        .get())
                .collect(Collectors.toList());

        practice.setStudentAnswers(answers);

        BigDecimal totalScore = calculateTotalScore(practice);

        practice.setTotalScore(totalScore);
        practice.setSubmitTime(LocalDateTime.now());
        practice.setStatus(StudentPractice.PracticeStatus.SUBMITTED);

        studentPracticeRepository.save(practice);

        System.out.println("[SUBMIT] Practice " + practiceId + " submitted with " + answers.size() + " unique questions.");

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
    @Transactional
    public StudentEnrollResponse enrollStudent(StudentEnrollRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        PracticeSession session = practiceSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập này.", 404));

        if (!session.getSessionCode().equals(request.getSessionCode())) {
            throw new ApiException("INVALID_SESSION_CODE", "Mã code không khớp với buổi luyện tập.", 400);
        }

        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new ApiException("SESSION_INACTIVE", "Buổi luyện tập này hiện không hoạt động.", 400);
        }

        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(vnZone);
        LocalDateTime start = session.getExamDate();
        LocalDateTime end = start.plusMinutes(session.getDurationMinutes());

        System.out.println("[ENROLL] Giờ hiện tại VN: " + now);
        System.out.println("[ENROLL] Giờ bắt đầu: " + start);
        System.out.println("[ENROLL] Giờ kết thúc: " + end);

        if (now.isBefore(start)) {
            throw new ApiException("SESSION_NOT_STARTED", "Bài thi chưa đến thời gian bắt đầu.", 400);
        }
        if (now.isAfter(end)) {
            throw new ApiException("SESSION_ENDED", "Bài thi đã kết thúc.", 400);
        }

        Optional<StudentPractice> existingPractice =
                studentPracticeRepository.findTopByPracticeSessionAndStudentOrderByAttemptNumberDesc(session, student);

        StudentPractice practice;

        if (existingPractice.isPresent()) {
            StudentPractice latest = existingPractice.get();
            if (latest.getStatus() == StudentPractice.PracticeStatus.SUBMITTED) {
                if (latest.getAttemptNumber() >= session.getAttemptLimit()) {
                    throw new ApiException("MAX_ATTEMPTS_REACHED", "Bạn đã đạt giới hạn số lần thi cho buổi luyện tập này.", 400);
                }
                // Create new attempt
                practice = StudentPractice.builder()
                        .practiceSession(session)
                        .student(student)
                        .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                        .perTime(now)
                        .attemptNumber(latest.getAttemptNumber() + 1)
                        .examCode(request.getSessionCode())
                        .build();
                studentPracticeRepository.save(practice);
                System.out.println("[ENROLL] New attempt for student. PracticeId: " + practice.getPracticeId() + ", Attempt: " + practice.getAttemptNumber());
            } else {
                // Re-entering existing in-progress attempt
                practice = latest;
                System.out.println("[ENROLL] Student re-entering exam. PracticeId: " + practice.getPracticeId());
            }
        } else {
            practice = StudentPractice.builder()
                    .practiceSession(session)
                    .student(student)
                    .status(StudentPractice.PracticeStatus.IN_PROGRESS)
                    .perTime(now)
                    .attemptNumber(1)
                    .examCode(request.getSessionCode())
                    .build();

            studentPracticeRepository.save(practice);

            if (session.getCurrentParticipants() == null) session.setCurrentParticipants(0);
            session.setCurrentParticipants(session.getCurrentParticipants() + 1);
            practiceSessionRepository.save(session);

            System.out.println("[ENROLL] New student enrolled. PracticeId: " + practice.getPracticeId());
        }

        return StudentEnrollResponse.builder()
                .practiceId(practice.getPracticeId())
                .sessionId(session.getSessionId())
                .sessionName(session.getSessionName())
                .startTime(start)
                .endTime(end)
                .status(practice.getStatus().name())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<StudentRankingResponse> getRankings(String order) {
        Sort sort = Sort.by("asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC, "totalScore");
        List<StudentPractice> practices = studentPracticeRepository.findByStatus(StudentPractice.PracticeStatus.SUBMITTED, sort);
        return practices.stream()
                .map(p -> {
                    StudentRankingResponse r = new StudentRankingResponse();
                    r.setStudentName(p.getStudent().getUsername());
                    r.setScore(p.getTotalScore().doubleValue());
                    r.setSubmitTime(p.getSubmitTime());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAnswerDetailResponse> getStudentAnswersDetails(String sessonCode) {
        List<Object[]> results = studentAnswersRepository.findStudentAnswerDetails(sessonCode);
        return results.stream()
                .map(row -> {
                    StudentAnswerDetailResponse r = new StudentAnswerDetailResponse();
                    r.setStudentName((String) row[0]);
                    r.setSessionName((String) row[1]);
                    r.setQuestionText((String) row[2]);
                    r.setAnswerText((String) row[3]);
                    r.setIsCorrect((Boolean) row[4]);
                    return r;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalScore(StudentPractice practice) {
        if (practice.getStudentAnswers() == null || practice.getStudentAnswers().isEmpty())
            return BigDecimal.ZERO;

        BigDecimal totalScore = BigDecimal.ZERO;

        for (StudentAnswers answer : practice.getStudentAnswers()) {
            Questions question = answer.getQuestion();
            if (question == null) continue;

            Optional<MatrixQuestion> mqOpt = matrixQuestionRepository.findByQuestion_QuestionId(question.getQuestionId())
                    .stream().findFirst();

            BigDecimal marksAllocated = mqOpt.map(MatrixQuestion::getMarksAllocated).orElse(BigDecimal.ZERO);

            BigDecimal baseScore;
            if (marksAllocated.compareTo(BigDecimal.ZERO) == 0) {
                Double diff = question.getLevel() != null ? question.getLevel().getDifficultyScore() : 1.0;
                baseScore = BigDecimal.valueOf(diff);
            } else {
                baseScore = marksAllocated;
            }

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
