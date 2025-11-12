package sum25.studentcode.backend.modules.MatrixQuestion.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateMarksRequest {
    private BigDecimal marksAllocated;
}
