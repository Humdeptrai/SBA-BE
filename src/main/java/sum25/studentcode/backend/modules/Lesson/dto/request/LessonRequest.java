package sum25.studentcode.backend.modules.Lesson.dto.request;

import lombok.Data;

@Data
public class LessonRequest {
    private Long gradeId;
    private String lessonTitle;
    private String lessonContent;
    private String lessonObjectives;
    private Long subjectId;
}