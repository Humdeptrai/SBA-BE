package sum25.studentcode.backend.modules.QuestionType.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionTypeResponse {

    private Long questionTypeId;
    private String typeName;
    private String description;
    private Boolean enabledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
