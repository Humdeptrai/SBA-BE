package sum25.studentcode.backend.modules.MatrixQuestion.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MatrixQuestionRequest {
    private Long matrixId;
    private List<Long> questionIds;  // ✅ Cho phép add nhiều câu
    private BigDecimal marksAllocated; // ✅ Điểm cho mỗi câu (có thể null)
}
