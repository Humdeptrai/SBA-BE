package sum25.studentcode.backend.modules.Lesson.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonGradeResponse {
    private Long lessonId;
    private String lessonTitle;
}
