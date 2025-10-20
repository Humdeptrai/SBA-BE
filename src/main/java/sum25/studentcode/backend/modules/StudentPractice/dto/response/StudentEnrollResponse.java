package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentEnrollResponse {
    private Long practiceId;
    private Long sessionId;
    private String sessionName;
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
