package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionResponse {
    private Long questionId;
    private String questionText;
    private List<OptionItem> options; // chỉ id, text, order

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionItem {
        private Long optionId;
        private String optionText;
        private Integer optionOrder;
    }
}
