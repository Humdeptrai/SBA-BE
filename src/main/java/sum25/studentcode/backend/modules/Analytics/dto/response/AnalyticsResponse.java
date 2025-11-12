package sum25.studentcode.backend.modules.Analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private List<QuestionStatisticsResponse> topCorrectQuestions;
    private List<QuestionStatisticsResponse> topIncorrectQuestions;
    private List<QuestionStatisticsResponse> allQuestionsStatistics;
}

