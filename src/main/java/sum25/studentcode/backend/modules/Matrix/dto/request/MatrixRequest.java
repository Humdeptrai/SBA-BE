package sum25.studentcode.backend.modules.Matrix.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class MatrixRequest {
    private String matrixName;
    private String description;
    private String distribution;
    private List<AgentResult> agentResult;

    @Data
    public static class AgentResult {
        private Long lessonId;
        private Long levelId;
        private String lessonName;
        private String levelName;
        private Integer requiredCount;
        private String questionIds;
        private Integer actualCount;
    }
}
