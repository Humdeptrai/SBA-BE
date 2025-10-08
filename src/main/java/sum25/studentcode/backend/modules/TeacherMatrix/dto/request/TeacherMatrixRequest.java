package sum25.studentcode.backend.modules.TeacherMatrix.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TeacherMatrixRequest {
    private Long teacherId;
    private Long matrixId;
    private BigDecimal grade;
    private LocalDateTime assignmentDate;
}