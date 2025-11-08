package sum25.studentcode.backend.modules.Matrix.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatrixResponse {
    private Long matrixId;
    private String matrixName;
    private String description;
    private Integer totalQuestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}