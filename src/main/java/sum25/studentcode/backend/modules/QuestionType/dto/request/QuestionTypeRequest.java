package sum25.studentcode.backend.modules.QuestionType.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionTypeRequest {

    @NotBlank(message = "Type name is required")
    private String typeName;

    private String description;

    private Boolean enabledAt; // true/false (loại câu hỏi có đang được bật không)
}
