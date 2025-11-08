package sum25.studentcode.backend.modules.MatrixQuestion.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MatrixQuestionRequest {
    private Long matrixId;
    private List<Long> questionIds;  // ✅ Cho phép add nhiều câu

    @DecimalMin(value = "0.00", message = "Điểm phải lớn hơn hoặc bằng 0.01")
    @DecimalMax(value = "100.00", message = "Điểm không được vượt quá 100")
    private BigDecimal marksAllocated; // ✅ Điểm cho mỗi câu (có thể null)
}
