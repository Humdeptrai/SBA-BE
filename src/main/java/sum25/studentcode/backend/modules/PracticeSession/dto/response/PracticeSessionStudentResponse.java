package sum25.studentcode.backend.modules.PracticeSession.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PracticeSessionStudentResponse {
    private Long sessionId;
    private Long teacherId;
    private String sessionName;
    private String description;
    private Boolean isActive;
    private Integer maxParticipants;
    private LocalDateTime examDate;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
