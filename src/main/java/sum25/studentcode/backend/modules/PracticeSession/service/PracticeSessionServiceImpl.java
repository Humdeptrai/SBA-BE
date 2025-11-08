package sum25.studentcode.backend.modules.PracticeSession.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Auth.repository.UserRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionStudentResponse;
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

    @Override
    public PracticeSessionResponse createPracticeSession(PracticeSessionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));


        PracticeSession session = PracticeSession.builder()
//                .lesson(lesson)
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

    @Override
    public PracticeSessionResponse getPracticeSessionById(Long id) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));
        return convertToResponse(session);
    }

    @Override
    public List<PracticeSessionResponse> getAllPracticeSessions() {
        List<PracticeSession> list = practiceSessionRepository.findAll();
        if (list.isEmpty())
            throw new ApiException("EMPTY_LIST", "Chưa có buổi luyện tập nào.", 404);

        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<PracticeSessionStudentResponse> getAllPracticeSessionsForStudents() {
        List<PracticeSession> list = practiceSessionRepository.findAll();
        if (list.isEmpty())
            throw new ApiException("EMPTY_LIST", "Chưa có buổi luyện tập nào.", 404);

        return list.stream().map(this::convertToStudentResponse).collect(Collectors.toList());
    }

    @Override
    public PracticeSessionResponse updatePracticeSession(Long id, PracticeSessionRequest request) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        if (!session.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền cập nhật buổi luyện tập này.", 403);
        }

        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));


//        session.setLesson(lesson);
        session.setMatrix(matrix);
//        session.setTeacher(teacher); // Do not change teacher
        session.setSessionCode(request.getSessionCode());
        session.setSessionName(request.getSessionName());
        session.setDescription(request.getDescription());
        session.setMaxParticipants(request.getMaxParticipants());
        session.setExamDate(request.getExamDate());
        session.setDurationMinutes(request.getDurationMinutes());

        session = practiceSessionRepository.save(session);
        return convertToResponse(session);
    }

    @Override
    public void deletePracticeSession(Long id) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        if (!session.getTeacher().getUserId().equals(teacher.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền xóa buổi luyện tập này.", 403);
        }

        practiceSessionRepository.deleteById(id);
    }



    private PracticeSessionResponse convertToResponse(PracticeSession entity) {
        PracticeSessionResponse res = new PracticeSessionResponse();
//        res.setLessonId(entity.getLesson().getLessonId());
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

    private PracticeSessionStudentResponse convertToStudentResponse(PracticeSession entity) {
        PracticeSessionStudentResponse res = new PracticeSessionStudentResponse();
        res.setSessionId(entity.getSessionId());
        res.setTeacherId(entity.getTeacher().getUserId());
        res.setSessionName(entity.getSessionName());
        res.setDescription(entity.getDescription());
        res.setIsActive(entity.getIsActive());
        res.setMaxParticipants(entity.getMaxParticipants());
        res.setExamDate(entity.getExamDate());
        res.setDurationMinutes(entity.getDurationMinutes());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        return res;
    }
}
