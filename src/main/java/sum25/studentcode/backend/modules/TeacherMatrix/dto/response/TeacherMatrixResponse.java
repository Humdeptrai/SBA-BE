package sum25.studentcode.backend.modules.TeacherMatrix.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TeacherMatrixResponse {
    private Long teacherMatrixId;
    private Long teacherId;
    private Long matrixId;
    private BigDecimal grade;
    private LocalDateTime assignmentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}