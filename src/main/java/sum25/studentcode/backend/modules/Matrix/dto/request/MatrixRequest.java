package sum25.studentcode.backend.modules.Matrix.dto.request;

import lombok.Data;

@Data
public class MatrixRequest {
    private Long examId;
    private String matrixName;
    private String description;
    private Integer totalQuestions;
}