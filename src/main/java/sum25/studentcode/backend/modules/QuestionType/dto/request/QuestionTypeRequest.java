package sum25.studentcode.backend.modules.QuestionType.dto.request;

import lombok.Data;

@Data
public class QuestionTypeRequest {
    private String typeName;
    private String description;
    private Boolean enabledAt;
}