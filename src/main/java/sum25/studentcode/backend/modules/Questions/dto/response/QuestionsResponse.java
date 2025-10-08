package sum25.studentcode.backend.modules.Questions.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionsResponse {
    private Long questionId;
    private Long lessonId;
    private Long questionTypeId;
    private Long levelId;
    private String questionText;
    private String correctAnswer;
    private String explanation;
    private Long subjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}