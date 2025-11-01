package sum25.studentcode.backend.modules.Matrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.modules.Lesson.repository.LessonRepository;
import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatrixServiceImpl implements MatrixService {

    private final MatrixRepository matrixRepository;
    private final LessonRepository lessonRepository;

    @Override
    public MatrixResponse createMatrix(MatrixRequest request) {
        var lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ApiException("LESSON_NOT_FOUND", "Không tìm thấy bài học.", 404));

        // 🔍 Kiểm tra trùng tên trong cùng bài học
        if (matrixRepository.existsByMatrixNameAndLesson_LessonId(request.getMatrixName(), request.getLessonId())) {
            throw new ApiException("DUPLICATE_MATRIX_NAME",
                    String.format("Ma trận với tên '%s' đã tồn tại trong bài học này.", request.getMatrixName()), 400);
        }

        Matrix matrix = Matrix.builder()
                .matrixName(request.getMatrixName())
                .description(request.getDescription())
                .totalQuestions(request.getTotalQuestions())
                .lesson(lesson)
                .build();

        matrix = matrixRepository.save(matrix);
        return convertToResponse(matrix);
    }


    /** ✅ Lấy chi tiết ma trận */
    @Override
    public MatrixResponse getMatrixById(Long id) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));
        return convertToResponse(matrix);
    }

    /** ✅ Lấy danh sách tất cả ma trận */
    @Override
    public List<MatrixResponse> getAllMatrices() {
        List<Matrix> matrices = matrixRepository.findAll();
        if (matrices.isEmpty()) {
            throw new ApiException("EMPTY_LIST", "Chưa có ma trận nào.", 404);
        }
        return matrices.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /** ✅ Cập nhật ma trận */
    @Override
    public MatrixResponse updateMatrix(Long id, MatrixRequest request) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        Long targetLessonId = request.getLessonId() != null
                ? request.getLessonId()
                : (matrix.getLesson() != null ? matrix.getLesson().getLessonId() : null);

        if (targetLessonId == null) {
            throw new ApiException("LESSON_ID_REQUIRED", "Ma trận phải thuộc về một bài học.", 400);
        }

        // 🔍 Kiểm tra trùng tên (trừ chính nó)
        if (matrixRepository.existsByMatrixNameAndLesson_LessonIdAndMatrixIdNot(
                request.getMatrixName(), targetLessonId, id)) {
            throw new ApiException("DUPLICATE_MATRIX_NAME",
                    String.format("Đã tồn tại ma trận với tên '%s' trong bài học này.", request.getMatrixName()), 400);
        }

        matrix.setMatrixName(request.getMatrixName());
        matrix.setDescription(request.getDescription());
        matrix.setTotalQuestions(request.getTotalQuestions());

        if (request.getLessonId() != null) {
            var lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ApiException("LESSON_NOT_FOUND", "Không tìm thấy bài học.", 404));
            matrix.setLesson(lesson);
        }

        matrix = matrixRepository.save(matrix);
        return convertToResponse(matrix);
    }


    /** ✅ Xoá ma trận */
    @Override
    public void deleteMatrix(Long id) {
        if (!matrixRepository.existsById(id)) {
            throw new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404);
        }
        matrixRepository.deleteById(id);
    }

    /** ✅ Convert Entity → DTO */
    private MatrixResponse convertToResponse(Matrix matrix) {
        MatrixResponse response = new MatrixResponse();
        response.setMatrixId(matrix.getMatrixId());
        response.setMatrixName(matrix.getMatrixName());
        response.setDescription(matrix.getDescription());
        response.setTotalQuestions(matrix.getTotalQuestions());
        response.setCreatedAt(matrix.getCreatedAt());
        response.setUpdatedAt(matrix.getUpdatedAt());
        response.setLessonId(matrix.getLesson() != null ? matrix.getLesson().getLessonId() : null);

        return response;
    }
}
