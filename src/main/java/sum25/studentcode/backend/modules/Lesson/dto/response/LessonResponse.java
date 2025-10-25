package sum25.studentcode.backend.modules.Lesson.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {

    private Long lessonId;
    private String lessonTitle;
    private String lessonContent;
    private String lessonObjectives;
    private Long gradeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
