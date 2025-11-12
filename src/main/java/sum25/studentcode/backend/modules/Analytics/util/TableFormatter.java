package sum25.studentcode.backend.modules.Analytics.util;

import sum25.studentcode.backend.modules.Analytics.dto.response.QuestionStatisticsResponse;

import java.math.BigDecimal;
import java.util.List;

public class TableFormatter {
    
    public static String formatQuestionStatisticsTable(List<QuestionStatisticsResponse> statistics, String title) {
        if (statistics == null || statistics.isEmpty()) {
            return title + "\nNo data available.\n";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(150)).append("\n");
        sb.append(title).append("\n");
        sb.append("=".repeat(150)).append("\n");
        
        // Header
        String header = String.format("%-8s | %-60s | %-20s | %-22s | %-15s | %-12s",
                "ID", "Question", "Correct Answers", "Incorrect Answers", "Total Answers", "Accuracy (%)");
        sb.append(header).append("\n");
        sb.append("-".repeat(150)).append("\n");
        
        // Rows
        for (QuestionStatisticsResponse stat : statistics) {
            String questionText = stat.getQuestionText();
            if (questionText != null && questionText.length() > 60) {
                questionText = questionText.substring(0, 57) + "...";
            }
            if (questionText == null) {
                questionText = "N/A";
            }
            
            BigDecimal accuracy = stat.getAccuracy();
            String accuracyStr = accuracy != null ? String.format("%.2f", accuracy) : "0.00";
            
            String row = String.format("%-8d | %-60s | %-20d | %-22d | %-15d | %-12s",
                    stat.getQuestionId(),
                    questionText,
                    stat.getCorrectAnswers() != null ? stat.getCorrectAnswers() : 0L,
                    stat.getIncorrectAnswers() != null ? stat.getIncorrectAnswers() : 0L,
                    stat.getTotalAnswers() != null ? stat.getTotalAnswers() : 0L,
                    accuracyStr);
            sb.append(row).append("\n");
        }
        
        sb.append("=".repeat(150)).append("\n");
        sb.append("Total records: ").append(statistics.size()).append("\n\n");
        
        return sb.toString();
    }
    
    public static String formatFullAnalyticsReport(List<QuestionStatisticsResponse> topCorrect,
                                                   List<QuestionStatisticsResponse> topIncorrect,
                                                   List<QuestionStatisticsResponse> allStats) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(formatQuestionStatisticsTable(topCorrect, "TOP 10 QUESTIONS WITH HIGHEST NUMBER OF CORRECT ANSWERS"));
        sb.append("\n\n");
        sb.append(formatQuestionStatisticsTable(topIncorrect, "TOP 10 QUESTIONS WITH HIGHEST NUMBER OF INCORRECT ANSWERS"));
        sb.append("\n\n");
        sb.append(formatQuestionStatisticsTable(allStats, "ALL QUESTIONS STATISTICS (Sorted by Correct Answers Count)"));
        
        return sb.toString();
    }
}

