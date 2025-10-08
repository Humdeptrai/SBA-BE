package sum25.studentcode.backend.modules.PracticeSession.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PracticeSessionResponse {
    private Long sessionId;
    private Long examId;
    private Long studentId;
    private String sessionCode;
    private Long teacherId;
    private String sessionName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}