package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;

@Data
public class StudentAnswerDetailResponse {
    private String studentName;
    private String sessionName;
    private String questionText;
    private String answerText;
    private Boolean isCorrect;
}
