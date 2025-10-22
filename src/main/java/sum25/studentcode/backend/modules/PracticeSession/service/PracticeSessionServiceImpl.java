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
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.PracticeSession.dto.request.PracticeSessionRequest;
import sum25.studentcode.backend.modules.PracticeSession.dto.response.PracticeSessionResponse;
import sum25.studentcode.backend.modules.PracticeSession.repository.PracticeSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final PracticeSessionRepository practiceSessionRepository;
    private final MatrixRepository matrixRepository;      // ✅ đổi từ examRepository → matrixRepository
    private final UserRepository userRepository;

    @Override
    public PracticeSessionResponse createPracticeSession(PracticeSessionRequest request) {
        // ✅ Lấy teacher từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        // ✅ Kiểm tra Matrix tồn tại
        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        // ✅ Validate thời gian
        if (request.getStartTime() == null || request.getEndTime() == null)
            throw new ApiException("INVALID_TIME", "Thời gian bắt đầu và kết thúc không được để trống.", 400);

        if (request.getStartTime().isAfter(request.getEndTime()))
            throw new ApiException("INVALID_TIME_RANGE", "Thời gian bắt đầu không thể sau thời gian kết thúc.", 400);

        PracticeSession session = PracticeSession.builder()
                .matrix(matrix)                                   // ✅ đổi từ exam → matrix
                .sessionCode(request.getSessionCode())
                .teacher(teacher)
                .sessionName(request.getSessionName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .maxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 50)
                .build();

        session = practiceSessionRepository.save(session);
        return convertToResponse(session);
    }

    @Override
    public PracticeSessionResponse getPracticeSessionById(Long id) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));

        if (LocalDateTime.now().isAfter(session.getEndTime()))
            throw new ApiException("SESSION_EXPIRED", "Buổi luyện tập đã hết hạn.", 400);

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
    public PracticeSessionResponse updatePracticeSession(Long id, PracticeSessionRequest request) {
        PracticeSession session = practiceSessionRepository.findById(id)
                .orElseThrow(() -> new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404));

        Matrix matrix = matrixRepository.findById(request.getMatrixId())
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Không tìm thấy người dùng từ token.", 404));

        if (request.getStartTime() != null && request.getEndTime() != null &&
                request.getStartTime().isAfter(request.getEndTime()))
            throw new ApiException("INVALID_TIME_RANGE", "Thời gian bắt đầu không thể sau thời gian kết thúc.", 400);

        session.setMatrix(matrix);                                // ✅ đổi exam → matrix
        session.setTeacher(teacher);
        session.setSessionCode(request.getSessionCode());
        session.setSessionName(request.getSessionName());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setIsActive(request.getIsActive());
        session.setMaxParticipants(request.getMaxParticipants());

        practiceSessionRepository.save(session);
        return convertToResponse(session);
    }

    @Override
    public void deletePracticeSession(Long id) {
        if (!practiceSessionRepository.existsById(id))
            throw new ApiException("SESSION_NOT_FOUND", "Không tìm thấy buổi luyện tập.", 404);

        practiceSessionRepository.deleteById(id);
    }

    private PracticeSessionResponse convertToResponse(PracticeSession entity) {
        PracticeSessionResponse res = new PracticeSessionResponse();
        res.setSessionId(entity.getSessionId());
        res.setMatrixId(entity.getMatrix().getMatrixId());                     // ✅ thêm matrixId
        res.setExamId(entity.getMatrix().getExam().getExamId());               // ✅ lấy examId từ matrix.exam
        res.setSessionCode(entity.getSessionCode());
        res.setTeacherId(entity.getTeacher().getUserId());
        res.setSessionName(entity.getSessionName());
        res.setStartTime(entity.getStartTime());
        res.setEndTime(entity.getEndTime());
        res.setIsActive(entity.getIsActive());
        res.setMaxParticipants(entity.getMaxParticipants());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        return res;
    }
}
