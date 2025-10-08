package sum25.studentcode.backend.modules.Options.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OptionsResponse {
    private Long optionId;
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;
    private Integer optionOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}