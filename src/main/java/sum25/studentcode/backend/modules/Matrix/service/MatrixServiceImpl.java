package sum25.studentcode.backend.modules.Matrix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.*;
import sum25.studentcode.backend.modules.Auth.service.UserService;
import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;
import sum25.studentcode.backend.modules.Matrix.repository.MatrixRepository;
import sum25.studentcode.backend.modules.MatrixQuestion.repository.MatrixQuestionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class MatrixServiceImpl implements MatrixService {

    private final MatrixRepository matrixRepository;
    private final MatrixQuestionRepository matrixQuestionRepository;

    private final UserService userService;


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

        List<MatrixAllocation> allocations = parseDistribution(request.getDistribution(), matrix);
        matrix.setMatrixAllocations(allocations);

        // Create a map for quick lookup of allocation by knowledgeLevel
        Map<String, MatrixAllocation> levelToAllocation = allocations.stream()
                .collect(Collectors.toMap(MatrixAllocation::getKnowledgeLevel, alloc -> alloc));

        // Create MatrixQuestion from agentResult
        for (MatrixRequest.AgentResult result : request.getAgentResult()) {
            MatrixAllocation allocation = levelToAllocation.get(result.getLevelName());
            if (allocation != null) {
                BigDecimal marksPerQuestion = allocation.getMarksAllocated()
                        .divide(BigDecimal.valueOf(allocation.getQuestionCount()), RoundingMode.HALF_UP);
                String[] questionIdStrings = result.getQuestionIds().split(",");
                for (String quesIdStr : questionIdStrings) {
                    Long questionId = Long.parseLong(quesIdStr.trim());
                    Questions question = new Questions();
                    question.setQuestionId(questionId);
                    MatrixQuestion matrixQuestion = MatrixQuestion.builder()
                            .matrix(matrix)
                            .question(question)
                            .marksAllocated(marksPerQuestion)
                            .build();
                    matrixQuestionRepository.save(matrixQuestion);
//                    matrix.getMatrixQuestions().add(matrixQuestion);
                }
            }
        }

        matrix = matrixRepository.save(matrix);
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

        // Re-parse distribution and update allocations
        List<MatrixAllocation> allocations = parseDistribution(request.getDistribution(), matrix);
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

    private List<MatrixAllocation> parseDistribution(String distribution, Matrix matrix) {
        List<MatrixAllocation> allocations = new ArrayList<>();
        int totalQuestions = matrix.getTotalQuestions();
        BigDecimal totalMarks = matrix.getTotalMarks();
        String[] pairs = distribution.split(",");
        for (String pair : pairs) {
            System.out.println("Distribution Pairs: "+ pair);

            String[] keyValue = pair.split(":");
            System.out.println("Distribution keyValue: "+ keyValue);

            if (keyValue.length == 2) {
                String knowledgeLevel = keyValue[0];
                BigDecimal percent = new BigDecimal(keyValue[1]);
                int questionCount = percent.multiply(BigDecimal.valueOf(totalQuestions)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).intValue();
                BigDecimal marksAllocated = percent.multiply(totalMarks).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

                MatrixAllocation allocation = MatrixAllocation.builder()
                        .matrix(matrix)
                        .knowledgeLevel(knowledgeLevel)
                        .percentAllocation(percent)
                        .questionCount(questionCount)
                        .marksAllocated(marksAllocated)
                        .build();
                allocations.add(allocation);
            } else {
                throw new ApiException("INVALID_DISTRIBUTION_FORMAT", "Định dạng phân phối không hợp lệ.", 400);
            }
        }
        return allocations;
    }
}
