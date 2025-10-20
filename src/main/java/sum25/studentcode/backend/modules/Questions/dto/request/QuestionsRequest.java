package sum25.studentcode.backend.modules.Questions.dto.request;

import lombok.Data;

@Data
public class QuestionsRequest {
    private Long lessonId;
    private Long questionTypeId;
    private Long levelId;
    private String questionText;
    private String correctAnswer;
    private String explanation;
}