package sum25.studentcode.backend.modules.Exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.modules.Exam.dto.request.ExamRequest;
import sum25.studentcode.backend.modules.Exam.dto.response.ExamResponse;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {

        // ✅ Kiểm tra trùng examCode
        if (examRepository.existsByExamCode(request.getExamCode())) {
            throw new ApiException(
                    "EXAM_CODE_DUPLICATE",
                    "Mã bài thi đã tồn tại",
                    "Mã bài thi '" + request.getExamCode() + "' đã được sử dụng. Vui lòng chọn mã khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Kiểm tra trùng examName
        if (examRepository.existsByExamName(request.getExamName())) {
            throw new ApiException(
                    "EXAM_NAME_DUPLICATE",
                    "Tên bài thi đã tồn tại",
                    "Tên bài thi '" + request.getExamName() + "' đã được sử dụng. Vui lòng chọn tên khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        Exam exam = Exam.builder()
                .examName(request.getExamName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .examDate(request.getExamDate())
                .examCode(request.getExamCode())
                .isActive(true)
                .build();

        exam = examRepository.save(exam);
        return convertToResponse(exam);
    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "EXAM_NOT_FOUND",
                        "Không tìm thấy bài thi",
                        "Không tìm thấy bài thi với ID = " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return convertToResponse(exam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "EXAM_NOT_FOUND",
                        "Không tìm thấy bài thi",
                        "Không tìm thấy bài thi với ID = " + id,
                        HttpStatus.NOT_FOUND.value()
                ));

        // ✅ Kiểm tra trùng code nếu đổi
        if (!exam.getExamCode().equals(request.getExamCode())
                && examRepository.existsByExamCode(request.getExamCode())) {
            throw new ApiException(
                    "EXAM_CODE_DUPLICATE",
                    "Mã bài thi đã tồn tại",
                    "Mã bài thi '" + request.getExamCode() + "' đã được sử dụng. Vui lòng chọn mã khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Kiểm tra trùng name nếu đổi
        if (!exam.getExamName().equals(request.getExamName())
                && examRepository.existsByExamName(request.getExamName())) {
            throw new ApiException(
                    "EXAM_NAME_DUPLICATE",
                    "Tên bài thi đã tồn tại",
                    "Tên bài thi '" + request.getExamName() + "' đã được sử dụng. Vui lòng chọn tên khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        exam.setExamName(request.getExamName());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setExamDate(request.getExamDate());
        exam.setExamCode(request.getExamCode());
        exam = examRepository.save(exam);
        return convertToResponse(exam);
    }

    @Override
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ApiException(
                    "EXAM_NOT_FOUND",
                    "Không tìm thấy bài thi",
                    "Không tìm thấy bài thi với ID = " + id,
                    HttpStatus.NOT_FOUND.value()
            );
        }
        examRepository.deleteById(id);
    }

    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setExamId(exam.getExamId());
        response.setExamName(exam.getExamName());
        response.setExamCode(exam.getExamCode());
        response.setDescription(exam.getDescription());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setExamDate(exam.getExamDate());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        return response;
    }
}
