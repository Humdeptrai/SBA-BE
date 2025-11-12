package sum25.studentcode.backend.modules.Matrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixAllocateRepository;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class MatrixServiceImpl implements MatrixService {

    private final MatrixRepository matrixRepository;
    private final MatrixQuestionRepository matrixQuestionRepository;
    private final UserService userService;
    private final MatrixAllocateRepository matrixAllocateRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public MatrixResponse createMatrix(MatrixRequest request) {
        // Calculate totalQuestions from agentResult
        int totalQuestions = request.getAgentResult().stream()
                .mapToInt(MatrixRequest.AgentResult::getActualCount)
                .sum();
        BigDecimal totalMarks = BigDecimal.valueOf(totalQuestions);

        User user = userService.getCurrentUser();
        Matrix matrix = Matrix.builder()
                .matrixName(request.getMatrixName())
                .description(request.getDescription())
                .totalQuestions(totalQuestions)
                .createdBy(user)
                .totalMarks(totalMarks)
                .build();

        // Save matrix first to get ID
        matrix = matrixRepository.save(matrix);
        final Matrix savedMatrix = matrix;

        // Save MatrixAllocation from allocates (tỉ lệ phân bổ từ client)
        List<MatrixAllocation> allocations = parseAllocations(request.getAllocates(), savedMatrix);
        matrixAllocateRepository.saveAll(allocations);
        savedMatrix.setMatrixAllocations(allocations);

        // Save MatrixQuestion from agentResult (câu hỏi thực tế)
        // Calculate marks per question based on total marks and total questions
        BigDecimal marksPerQuestion = totalMarks.divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);

        for (MatrixRequest.AgentResult result : request.getAgentResult()) {
            String[] questionIdStrings = result.getQuestionIds().split(",");
            for (String quesIdStr : questionIdStrings) {
                Long questionId = Long.parseLong(quesIdStr.trim());
                // Load question from database, skip if not found
                questionsRepository.findById(questionId).ifPresent(question -> {
                    MatrixQuestion matrixQuestion = MatrixQuestion.builder()
                            .matrix(savedMatrix)
                            .question(question)
                            .marksAllocated(marksPerQuestion)
                            .build();
                    matrixQuestionRepository.save(matrixQuestion);
                });
            }
        }

        return convertToResponse(matrix);
    }


    @Override
    public MatrixResponse getMatrixById(Long id) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));
        return convertToResponse(matrix);
    }

    @Override
    public List<MatrixResponse> getAllMatrices(Long userId) {
        List<Matrix> matrices = matrixRepository.findAllByCreatedBy_UserId(userId);
        if(matrices.isEmpty()) {
            throw new ApiException("EMPTY_LIST", "Chưa có ma trận nào.", 404);
        }
        return matrices.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public MatrixResponse updateMatrix(Long id, MatrixRequest request) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404));

        // Calculate totalQuestions from agentResult
        int totalQuestions = request.getAgentResult().stream()
                .mapToInt(MatrixRequest.AgentResult::getActualCount)
                .sum();
        BigDecimal totalMarks = BigDecimal.valueOf(totalQuestions);

        matrix.setMatrixName(request.getMatrixName());
        matrix.setDescription(request.getDescription());
        matrix.setTotalQuestions(totalQuestions);
        matrix.setTotalMarks(totalMarks);

        // Re-parse allocations and update
        List<MatrixAllocation> allocations = parseAllocations(request.getAllocates(), matrix);
        matrix.getMatrixAllocations().clear();
        matrix.getMatrixAllocations().addAll(allocations);

        matrix = matrixRepository.save(matrix);
        return convertToResponse(matrix);
    }


    @Override
    public void deleteMatrix(Long id) {
        if (!matrixRepository.existsById(id)) {
            throw new ApiException("MATRIX_NOT_FOUND", "Không tìm thấy ma trận đề thi.", 404);
        }
        matrixRepository.deleteById(id);
    }

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

    private List<MatrixAllocation> parseAllocations(List<MatrixRequest.Allocate> allocates, Matrix matrix) {
        List<MatrixAllocation> allocations = new ArrayList<>();

        if (allocates == null || allocates.isEmpty()) {
            throw new ApiException("INVALID_ALLOCATION", "Danh sách phân bổ không được để trống.", 400);
        }

        int totalQuestions = matrix.getTotalQuestions();
        BigDecimal totalMarks = matrix.getTotalMarks();

        for (MatrixRequest.Allocate allocate : allocates) {
            String knowledgeLevel = allocate.getAllocateName();
            BigDecimal percent = new BigDecimal(allocate.getPercent().replace("%", "").trim());

            int questionCount = percent.multiply(BigDecimal.valueOf(totalQuestions))
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).intValue();
            BigDecimal marksAllocated = percent.multiply(totalMarks)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            MatrixAllocation allocation = MatrixAllocation.builder()
                    .matrix(matrix)
                    .knowledgeLevel(knowledgeLevel)
                    .percentAllocation(percent)
                    .questionCount(questionCount)
                    .marksAllocated(marksAllocated)
                    .build();
            allocations.add(allocation);
        }

        return allocations;
    }
}
