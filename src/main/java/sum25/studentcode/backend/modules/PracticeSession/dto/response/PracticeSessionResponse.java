package sum25.studentcode.backend.modules.PracticeSession.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeSessionResponse {
    private Long lessonId;
    private Long sessionId;
    private Long matrixId;
    private String matrixName;
    private String sessionCode;
    private Long teacherId;
    private String sessionName;
    private String description;
    private Boolean isActive;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime examDate;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
