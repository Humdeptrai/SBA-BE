package sum25.studentcode.backend.modules.PracticeSession.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final PracticeSessionRepository practiceSessionRepository;
    private final MatrixRepository matrixRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    /** ✅ Tạo buổi luyện tập / bài thi mới */
    @Override
    public PracticeSessionResponse createPracticeSession(PracticeSessionRequest request) {
        // ✅ Lấy teacher từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        // ✅ Kiểm tra Lesson tồn tại
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ApiException("LESSON_NOT_FOUND", "Không tìm thấy bài học.", 404));

        // ✅ Kiểm tra Matrix tồn tại
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        // ✅ Check trùng tên trong cùng 1 Lesson
        boolean exists = practiceSessionRepository
                .existsByLesson_LessonIdAndSessionNameIgnoreCase(request.getLessonId(), request.getSessionName());
        if (exists) {
            throw new ApiException("DUPLICATE_SESSION_NAME",
                    "Tên buổi luyện tập đã tồn tại trong bài học này.", 400);
        }

        // ✅ Tạo mới session (isActive mặc định true)
        PracticeSession session = PracticeSession.builder()
                .lesson(lesson)
                .matrix(matrix)
                .teacher(teacher)
                .sessionCode(request.getSessionCode())
                .sessionName(request.getSessionName())
                .description(request.getDescription())
                .isActive(true)
                .maxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 50)
                .currentParticipants(0)
                .examDate(request.getExamDate())
                .durationMinutes(request.getDurationMinutes())
                .build();

        session = practiceSessionRepository.save(session);
        return convertToResponse(session);
    }

    /** ✅ Lấy chi tiết buổi luyện tập */
    @Override
    public PracticeSessionResponse getPracticeSessionById(Long id) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));
        return convertToResponse(session);
    }

    /** ✅ Lấy danh sách tất cả buổi luyện tập */
    @Override
    public List<PracticeSessionResponse> getAllPracticeSessions() {
        List<PracticeSession> list = practiceSessionRepository.findAll();
        if (list.isEmpty())
            throw new ApiException("EMPTY_LIST", "Chưa có buổi luyện tập nào.", 404);

        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /** ✅ Cập nhật buổi luyện tập */
    @Override
    public PracticeSessionResponse updatePracticeSession(Long id, PracticeSessionRequest request) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));

        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ApiException("LESSON_NOT_FOUND", "Không tìm thấy bài học.", 404));

        // ✅ Check trùng tên trong lesson (trừ chính nó)
        boolean exists = practiceSessionRepository
                .existsByLesson_LessonIdAndSessionNameIgnoreCase(request.getLessonId(), request.getSessionName());
        if (exists && !session.getSessionName().equalsIgnoreCase(request.getSessionName())) {
            throw new ApiException("DUPLICATE_SESSION_NAME",
                    "Tên buổi luyện tập đã tồn tại trong bài học này.", 400);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        // ✅ Cập nhật thông tin
        session.setLesson(lesson);
        session.setMatrix(matrix);
        session.setTeacher(teacher);
        session.setSessionCode(request.getSessionCode());
        session.setSessionName(request.getSessionName());
        session.setDescription(request.getDescription());
        session.setMaxParticipants(request.getMaxParticipants());
        session.setExamDate(request.getExamDate());
        session.setDurationMinutes(request.getDurationMinutes());

        session = practiceSessionRepository.save(session);
        return convertToResponse(session);
    }

    /** ✅ Xóa buổi luyện tập */
    @Override
    public void deletePracticeSession(Long id) {
        if (!practiceSessionRepository.existsById(id))
            throw new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404);
        practiceSessionRepository.deleteById(id);
    }

    /** ✅ Convert sang Response DTO */
    private PracticeSessionResponse convertToResponse(PracticeSession entity) {
        PracticeSessionResponse res = new PracticeSessionResponse();
        res.setLessonId(entity.getLesson().getLessonId());
        res.setSessionId(entity.getSessionId());
        res.setMatrixId(entity.getMatrix().getMatrixId());
        res.setMatrixName(entity.getMatrix().getMatrixName());
        res.setSessionCode(entity.getSessionCode());
        res.setTeacherId(entity.getTeacher().getUserId());
        res.setSessionName(entity.getSessionName());
        res.setDescription(entity.getDescription());
        res.setIsActive(entity.getIsActive());
        res.setMaxParticipants(entity.getMaxParticipants());
        res.setCurrentParticipants(entity.getCurrentParticipants());
        res.setExamDate(entity.getExamDate());
        res.setDurationMinutes(entity.getDurationMinutes());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        return res;
    }
}
