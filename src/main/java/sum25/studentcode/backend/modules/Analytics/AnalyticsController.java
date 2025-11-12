package sum25.studentcode.backend.modules.Analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Analytics.dto.response.AnalyticsResponse;
import sum25.studentcode.backend.modules.Analytics.dto.response.QuestionStatisticsResponse;
import sum25.studentcode.backend.modules.Analytics.service.AnalyticsService;
import sum25.studentcode.backend.modules.Analytics.util.TableFormatter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/questions/statistics")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public AnalyticsResponse getQuestionStatistics() {
        return analyticsService.getQuestionStatistics();
    }

    @GetMapping(value = "/questions/statistics/table", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String getQuestionStatisticsTable() {
        AnalyticsResponse response = analyticsService.getQuestionStatistics();
        List<QuestionStatisticsResponse> allStats = response.getAllQuestionsStatistics().stream()
                .sorted((a, b) -> Long.compare(
                        b.getCorrectAnswers() != null ? b.getCorrectAnswers() : 0L,
                        a.getCorrectAnswers() != null ? a.getCorrectAnswers() : 0L))
                .collect(Collectors.toList());
        
        return TableFormatter.formatFullAnalyticsReport(
                response.getTopCorrectQuestions(),
                response.getTopIncorrectQuestions(),
                allStats
        );
    }

    @GetMapping("/questions/top-correct")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public List<QuestionStatisticsResponse> getTopQuestionsByCorrectAnswers(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return analyticsService.getTopQuestionsByCorrectAnswers(limit);
    }

    @GetMapping("/questions/top-incorrect")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public List<QuestionStatisticsResponse> getTopQuestionsByIncorrectAnswers(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return analyticsService.getTopQuestionsByIncorrectAnswers(limit);
    }

    @GetMapping("/questions/all-statistics")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public List<QuestionStatisticsResponse> getAllQuestionsStatistics() {
        return analyticsService.getAllQuestionsStatistics();
    }
}

