package sum25.studentcode.backend.modules.Options.dto.request;

import lombok.Data;

@Data
public class OptionsRequest {
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;
    private Integer optionOrder;
}