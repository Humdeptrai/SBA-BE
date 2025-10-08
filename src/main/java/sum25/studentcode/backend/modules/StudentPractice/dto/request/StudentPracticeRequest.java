package sum25.studentcode.backend.modules.StudentPractice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentPracticeRequest {
    private Long sessionId;
    private Long studentId;
    private LocalDateTime perTime;
    private LocalDateTime submitTime;
    private BigDecimal totalScore;
    private String status;
}