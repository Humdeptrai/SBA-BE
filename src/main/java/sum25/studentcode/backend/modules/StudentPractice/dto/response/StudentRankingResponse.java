package sum25.studentcode.backend.modules.StudentPractice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentRankingResponse {
    private String studentName;
    private Double score;
    private LocalDateTime submitTime;
}
