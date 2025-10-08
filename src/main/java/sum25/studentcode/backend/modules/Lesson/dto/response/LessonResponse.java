package sum25.studentcode.backend.modules.Lesson.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonResponse {
    private Long lessonId;
    private Long gradeId;
    private String lessonTitle;
    private String lessonContent;
    private String lessonObjectives;
    private Long subjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}