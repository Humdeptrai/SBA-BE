package sum25.studentcode.backend.modules.Exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.modules.Exam.dto.request.ExamRequest;
import sum25.studentcode.backend.modules.Exam.dto.response.ExamResponse;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final LessonRepository lessonRepository; // ✅ thêm để lấy lesson

    @Override
    public ExamResponse createExam(ExamRequest request) {

        // ✅ Kiểm tra trùng examCode
        if (examRepository.existsByExamCode(request.getExamCode())) {
            throw new ApiException(
                    "EXAM_CODE_DUPLICATE",
                    "Mã bài thi đã tồn tại.",
                    "Mã bài thi '" + request.getExamCode() + "' đã được sử dụng. Vui lòng chọn mã khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Kiểm tra trùng examName
        if (examRepository.existsByExamName(request.getExamName())) {
            throw new ApiException(
                    "EXAM_NAME_DUPLICATE",
                    "Tên bài thi đã tồn tại.",
                    "Tên bài thi '" + request.getExamName() + "' đã được sử dụng. Vui lòng chọn tên khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Bắt buộc phải có lessonId
        if (request.getLessonId() == null) {
            throw new ApiException(
                    "LESSON_REQUIRED",
                    "Cần chỉ định bài học (lessonId) khi tạo bài thi.",
                    400
            );
        }

        // ✅ Lấy thông tin Lesson
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ApiException(
                        "LESSON_NOT_FOUND",
                        "Không tìm thấy bài học tương ứng với ID = " + request.getLessonId(),
                        404
                ));

        // ✅ Tạo Exam mới
        Exam exam = Exam.builder()
                .examName(request.getExamName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .examDate(request.getExamDate())
                .examCode(request.getExamCode())
                .lesson(lesson) // gắn vào Lesson
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
                        "Không tìm thấy bài thi.",
                        "Không tìm thấy bài thi với ID = " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return convertToResponse(exam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "EXAM_NOT_FOUND",
                        "Không tìm thấy bài thi.",
                        "Không tìm thấy bài thi với ID = " + id,
                        HttpStatus.NOT_FOUND.value()
                ));

        // ✅ Kiểm tra trùng code nếu đổi
        if (!exam.getExamCode().equals(request.getExamCode())
                && examRepository.existsByExamCode(request.getExamCode())) {
            throw new ApiException(
                    "EXAM_CODE_DUPLICATE",
                    "Mã bài thi đã tồn tại.",
                    "Mã bài thi '" + request.getExamCode() + "' đã được sử dụng. Vui lòng chọn mã khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Kiểm tra trùng name nếu đổi
        if (!exam.getExamName().equals(request.getExamName())
                && examRepository.existsByExamName(request.getExamName())) {
            throw new ApiException(
                    "EXAM_NAME_DUPLICATE",
                    "Tên bài thi đã tồn tại.",
                    "Tên bài thi '" + request.getExamName() + "' đã được sử dụng. Vui lòng chọn tên khác.",
                    HttpStatus.CONFLICT.value()
            );
        }

        // ✅ Cập nhật thông tin bài học (nếu có)
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ApiException(
                            "LESSON_NOT_FOUND",
                            "Không tìm thấy bài học tương ứng với ID = " + request.getLessonId(),
                            404
                    ));
            exam.setLesson(lesson);
        }

        // ✅ Cập nhật thông tin bài thi
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
                    "Không tìm thấy bài thi.",
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

        // ✅ Gắn thêm lessonId (nếu có)
        if (exam.getLesson() != null) {
            response.setLessonId(exam.getLesson().getLessonId());
        } else {
            response.setLessonId(null);
        }

        return response;
    }

    @Override
    public List<ExamResponse> getExamsByLesson(Long lessonId) {
        // ✅ Kiểm tra bài học tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException(
                        "LESSON_NOT_FOUND",
                        "Không tìm thấy bài học với ID = " + lessonId,
                        404
                ));

        // ✅ Tìm tất cả bài thi thuộc bài học đó
        List<Exam> exams = examRepository.findByLesson_LessonId(lessonId);

        return exams.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

}
