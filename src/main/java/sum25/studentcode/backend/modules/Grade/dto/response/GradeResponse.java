package sum25.studentcode.backend.modules.Grade.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeResponse {
    private Long gradeId;
    private String gradeLevel;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}