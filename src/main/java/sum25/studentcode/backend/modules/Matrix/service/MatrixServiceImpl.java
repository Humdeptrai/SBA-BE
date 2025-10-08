package sum25.studentcode.backend.modules.Matrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Exam;
import sum25.studentcode.backend.model.Matrix;
import sum25.studentcode.backend.modules.Exam.repository.ExamRepository;
import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrixServiceImpl implements MatrixService {

    private final MatrixRepository matrixRepository;
    private final ExamRepository examRepository;

    @Override
    public MatrixResponse createMatrix(MatrixRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Matrix matrix = Matrix.builder()
                .exam(exam)
                .matrixName(request.getMatrixName())
                .description(request.getDescription())
                .totalQuestions(request.getTotalQuestions())
                .build();
        matrix = matrixRepository.save(matrix);
        return convertToResponse(matrix);
    }

    @Override
    public MatrixResponse getMatrixById(Long id) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        return convertToResponse(matrix);
    }

    @Override
    public List<MatrixResponse> getAllMatrices() {
        return matrixRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MatrixResponse updateMatrix(Long id, MatrixRequest request) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        matrix.setExam(exam);
        matrix.setMatrixName(request.getMatrixName());
        matrix.setDescription(request.getDescription());
        matrix.setTotalQuestions(request.getTotalQuestions());
        matrix = matrixRepository.save(matrix);
        return convertToResponse(matrix);
    }

    @Override
    public void deleteMatrix(Long id) {
        if (!matrixRepository.existsById(id)) {
            throw new RuntimeException("Matrix not found");
        }
        matrixRepository.deleteById(id);
    }

    private MatrixResponse convertToResponse(Matrix matrix) {
        MatrixResponse response = new MatrixResponse();
        response.setMatrixId(matrix.getMatrixId());
        response.setExamId(matrix.getExam().getExamId());
        response.setMatrixName(matrix.getMatrixName());
        response.setDescription(matrix.getDescription());
        response.setTotalQuestions(matrix.getTotalQuestions());
        response.setCreatedAt(matrix.getCreatedAt());
        response.setUpdatedAt(matrix.getUpdatedAt());
        return response;
    }
}