package sum25.studentcode.backend.modules.StudentAnswers.dto.request;

import lombok.Data;


@Data
public class StudentAnswersRequest {
    private Long practiceId;
    private Long questionId;
    private Long selectedOptionId;
}