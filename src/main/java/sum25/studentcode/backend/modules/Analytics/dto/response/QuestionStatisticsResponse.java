package sum25.studentcode.backend.modules.Analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatisticsResponse {
    private Long questionId;
    private String questionText;
    private Long correctAnswers;
    private Long incorrectAnswers;
    private BigDecimal accuracy;
    private Long totalAnswers;

    public BigDecimal getAccuracy() {
        if (totalAnswers == null || totalAnswers == 0) {
            return BigDecimal.ZERO;
        }
        if (accuracy == null) {
            accuracy = BigDecimal.valueOf(correctAnswers)
                    .divide(BigDecimal.valueOf(totalAnswers), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return accuracy;
    }

    public Long getTotalAnswers() {
        if (totalAnswers == null) {
            totalAnswers = correctAnswers + incorrectAnswers;
        }
        return totalAnswers;
    }
}

