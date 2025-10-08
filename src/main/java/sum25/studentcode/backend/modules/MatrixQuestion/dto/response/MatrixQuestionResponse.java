package sum25.studentcode.backend.modules.MatrixQuestion.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MatrixQuestionResponse {
    private Long matrixQuestionId;
    private Long matrixId;
    private Long questionId;
    private BigDecimal marksAllocated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}