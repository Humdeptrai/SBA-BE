package sum25.studentcode.backend.modules.Questions.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class QuestionsRequest {
    private Long lessonId;
    private Long questionTypeId;
    private Long levelId;
    private String questionText;
    private String correctAnswer;
    private String explanation;
    private KnowledgeLevel knowledgeLevel;  // Enum: RECALL, UNDERSTAND, APPLY, ANALYZE


    public  enum KnowledgeLevel {
        RECALL,
        UNDERSTAND,
        APPLY,
    }
}
