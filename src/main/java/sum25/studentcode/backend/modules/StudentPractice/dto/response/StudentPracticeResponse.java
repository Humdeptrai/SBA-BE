package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StudentPracticeResponse {
    private Long practiceId;
    private Long sessionId;
    private String practiceSession;
    private String sessionName;
    private Long studentId;
    private LocalDateTime perTime;
    private LocalDateTime submitTime;
    private BigDecimal totalScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for JPQL 'new' expression
    public StudentPracticeResponse(Long practiceId, Long sessionId, Long studentId,
                                   LocalDateTime perTime, LocalDateTime submitTime,
                                   BigDecimal totalScore, String status,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.practiceId = practiceId;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.perTime = perTime;
        this.submitTime = submitTime;
        this.totalScore = totalScore;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}