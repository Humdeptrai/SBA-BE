package sum25.studentcode.backend.modules.QuestionType.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionTypeResponse {
    private Long questionTypeId;
    private String typeName;
    private String description;
    private Boolean enabledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}