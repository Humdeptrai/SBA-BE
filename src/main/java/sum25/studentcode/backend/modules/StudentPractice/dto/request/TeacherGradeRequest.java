package sum25.studentcode.backend.modules.StudentPractice.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TeacherGradeRequest {
    private BigDecimal totalScore; // ✅ điểm giáo viên chấm
}
