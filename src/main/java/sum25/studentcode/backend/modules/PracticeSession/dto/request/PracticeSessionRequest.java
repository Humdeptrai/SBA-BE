package sum25.studentcode.backend.modules.PracticeSession.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeSessionRequest {
    private Long matrixId;              // ✅ đổi từ examId → matrixId
    private String sessionCode;
    private String sessionName;
    private Boolean isActive;
    private Integer maxParticipants;
}
