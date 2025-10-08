package sum25.studentcode.backend.modules.Exam.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRequest {
    private String examName;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime examDate;
    private Long subjectId;
}