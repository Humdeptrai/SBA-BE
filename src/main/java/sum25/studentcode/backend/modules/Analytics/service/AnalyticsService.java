package sum25.studentcode.backend.modules.Analytics.service;

import sum25.studentcode.backend.modules.Analytics.dto.response.AnalyticsResponse;
import sum25.studentcode.backend.modules.Analytics.dto.response.QuestionStatisticsResponse;

import java.util.List;

public interface AnalyticsService {
    AnalyticsResponse getQuestionStatistics();
    List<QuestionStatisticsResponse> getTopQuestionsByCorrectAnswers(int limit);
    List<QuestionStatisticsResponse> getTopQuestionsByIncorrectAnswers(int limit);
    List<QuestionStatisticsResponse> getAllQuestionsStatistics();
}

