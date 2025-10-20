package sum25.studentcode.backend.modules.PracticeSession.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PracticeSessionRequest {
    private Long examId;
    private String sessionCode;
    private Long teacherId;
    private String sessionName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private Integer maxParticipants;
}