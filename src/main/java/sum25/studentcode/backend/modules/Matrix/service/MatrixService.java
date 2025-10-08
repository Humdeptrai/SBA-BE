package sum25.studentcode.backend.modules.Matrix.service;

import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;

import java.util.List;

public interface MatrixService {
    MatrixResponse createMatrix(MatrixRequest request);
    MatrixResponse getMatrixById(Long id);
    List<MatrixResponse> getAllMatrices();
    MatrixResponse updateMatrix(Long id, MatrixRequest request);
    void deleteMatrix(Long id);
}