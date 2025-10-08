package sum25.studentcode.backend.modules.StudentAnswers.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StudentAnswersResponse {
    private Long answerId;
    private Long practiceId;
    private Long questionId;
    private Long selectedOptionId;
    private Boolean isCorrect;
    private BigDecimal marksEarned;
    private LocalDateTime answeredAt;
}