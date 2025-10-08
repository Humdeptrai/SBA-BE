package sum25.studentcode.backend.modules.Subject.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubjectResponse {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Integer creditId;
    private String syllabus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}