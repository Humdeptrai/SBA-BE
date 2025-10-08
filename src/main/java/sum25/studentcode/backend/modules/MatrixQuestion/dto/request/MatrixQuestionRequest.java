package sum25.studentcode.backend.modules.MatrixQuestion.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MatrixQuestionRequest {
    private Long matrixId;
    private Long questionId;
    private BigDecimal marksAllocated;
}