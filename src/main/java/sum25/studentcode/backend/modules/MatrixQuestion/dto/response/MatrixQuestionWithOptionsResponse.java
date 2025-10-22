package sum25.studentcode.backend.modules.MatrixQuestion.dto.response;

import lombok.Data;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MatrixQuestionWithOptionsResponse {
    private Long matrixQuestionId;
    private Long matrixId;
    private String matrixName;
    private Long questionId;
    private String questionText;
    private BigDecimal marksAllocated;
    private List<OptionsResponse> options; // ✅ thêm danh sách option
}
