package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentPracticeResponse {
    private Long practiceId;
    private Long sessionId;
    private Long studentId;
    private LocalDateTime perTime;
    private LocalDateTime submitTime;
    private BigDecimal totalScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}