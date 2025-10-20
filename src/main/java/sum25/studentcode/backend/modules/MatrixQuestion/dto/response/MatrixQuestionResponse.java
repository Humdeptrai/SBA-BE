package sum25.studentcode.backend.modules.MatrixQuestion.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MatrixQuestionResponse {
    private Long matrixQuestionId;
    private Long matrixId;
    private String matrixName;       // ✅ hiển thị tên ma trận
    private Long questionId;
    private String questionText;     // ✅ hiển thị nội dung câu hỏi
    private BigDecimal marksAllocated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
