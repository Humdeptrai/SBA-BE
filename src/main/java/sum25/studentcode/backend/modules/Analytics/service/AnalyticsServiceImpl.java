package sum25.studentcode.backend.modules.Analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.modules.Analytics.dto.response.AnalyticsResponse;
import sum25.studentcode.backend.modules.Analytics.dto.response.QuestionStatisticsResponse;
import sum25.studentcode.backend.modules.Questions.repository.QuestionsRepository;
import sum25.studentcode.backend.modules.StudentAnswers.repository.StudentAnswersRepository;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.model.StudentAnswers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final StudentAnswersRepository studentAnswersRepository;
    private final QuestionsRepository questionsRepository;

    @Override
    public AnalyticsResponse getQuestionStatistics() {
        List<QuestionStatisticsResponse> allStats = getAllQuestionsStatistics();
        List<QuestionStatisticsResponse> topCorrect = getTopQuestionsByCorrectAnswers(10);
        List<QuestionStatisticsResponse> topIncorrect = getTopQuestionsByIncorrectAnswers(10);

        return AnalyticsResponse.builder()
                .topCorrectQuestions(topCorrect)
                .topIncorrectQuestions(topIncorrect)
                .allQuestionsStatistics(allStats)
                .build();
    }

    @Override
    public List<QuestionStatisticsResponse> getTopQuestionsByCorrectAnswers(int limit) {
        List<QuestionStatisticsResponse> allStats = getAllQuestionsStatistics();
        return allStats.stream()
                .sorted((a, b) -> Long.compare(
                        b.getCorrectAnswers() != null ? b.getCorrectAnswers() : 0L,
                        a.getCorrectAnswers() != null ? a.getCorrectAnswers() : 0L))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionStatisticsResponse> getTopQuestionsByIncorrectAnswers(int limit) {
        List<QuestionStatisticsResponse> allStats = getAllQuestionsStatistics();
        return allStats.stream()
                .sorted((a, b) -> Long.compare(
                        b.getIncorrectAnswers() != null ? b.getIncorrectAnswers() : 0L,
                        a.getIncorrectAnswers() != null ? a.getIncorrectAnswers() : 0L))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionStatisticsResponse> getAllQuestionsStatistics() {
        // Get all student answers with questions fetched
        List<StudentAnswers> allAnswers = studentAnswersRepository.findAllWithQuestions();
        
        // Group by question and count correct/incorrect
        Map<Long, QuestionStats> questionStatsMap = new HashMap<>();
        
        for (StudentAnswers answer : allAnswers) {
            if (answer.getQuestion() == null) continue;
            
            Long questionId = answer.getQuestion().getQuestionId();
            QuestionStats stats = questionStatsMap.getOrDefault(questionId, new QuestionStats());
            stats.questionId = questionId;
            stats.questionText = answer.getQuestion().getQuestionText();
            
            if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                stats.correctCount++;
            } else if (Boolean.FALSE.equals(answer.getIsCorrect())) {
                stats.incorrectCount++;
            }
            
            questionStatsMap.put(questionId, stats);
        }
        
        // Convert to response DTOs
        List<QuestionStatisticsResponse> responses = new ArrayList<>();
        for (QuestionStats stats : questionStatsMap.values()) {
            long total = stats.correctCount + stats.incorrectCount;
            BigDecimal accuracy = total > 0 
                ? BigDecimal.valueOf(stats.correctCount)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
            
            QuestionStatisticsResponse response = QuestionStatisticsResponse.builder()
                    .questionId(stats.questionId)
                    .questionText(stats.questionText != null ? stats.questionText : "N/A")
                    .correctAnswers(stats.correctCount)
                    .incorrectAnswers(stats.incorrectCount)
                    .totalAnswers(total)
                    .accuracy(accuracy)
                    .build();
            
            responses.add(response);
        }
        
        // Also include questions that have no answers yet
        List<Questions> allQuestions = questionsRepository.findAll();
        Set<Long> questionsWithAnswers = questionStatsMap.keySet();
        
        for (Questions question : allQuestions) {
            if (!questionsWithAnswers.contains(question.getQuestionId())) {
                QuestionStatisticsResponse response = QuestionStatisticsResponse.builder()
                        .questionId(question.getQuestionId())
                        .questionText(question.getQuestionText() != null ? question.getQuestionText() : "N/A")
                        .correctAnswers(0L)
                        .incorrectAnswers(0L)
                        .totalAnswers(0L)
                        .accuracy(BigDecimal.ZERO)
                        .build();
                responses.add(response);
            }
        }
        
        return responses;
    }
    
    private static class QuestionStats {
        Long questionId;
        String questionText;
        long correctCount = 0;
        long incorrectCount = 0;
    }
}

