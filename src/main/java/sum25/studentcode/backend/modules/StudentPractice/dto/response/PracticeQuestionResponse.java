// dto/response/PracticeQuestionResponse.java
package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PracticeQuestionResponse {
    private Long questionId;
    private String questionText;
    private List<OptionItem> options;   // chỉ id, text, order

    @Data
    public static class OptionItem {
        private Long optionId;
        private String optionText;
        private Integer optionOrder;
    }
}
