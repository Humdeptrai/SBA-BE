package sum25.studentcode.backend.modules.Exam.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private Long examId;
    private String examName;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime examDate;
    private Long subjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}