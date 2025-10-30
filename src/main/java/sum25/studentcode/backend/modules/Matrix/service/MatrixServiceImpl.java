package sum25.studentcode.backend.modules.Matrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Matrix;
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

    /** ✅ Tạo ma trận câu hỏi mới */
    @Override
    public MatrixResponse createMatrix(MatrixRequest request) {
        Matrix matrix = Matrix.builder()
                .matrixName(request.getMatrixName())
                .description(request.getDescription())
                .totalQuestions(request.getTotalQuestions())
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

        matrix.setMatrixName(request.getMatrixName());
        matrix.setDescription(request.getDescription());
        matrix.setTotalQuestions(request.getTotalQuestions());

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
        return response;
    }
}
