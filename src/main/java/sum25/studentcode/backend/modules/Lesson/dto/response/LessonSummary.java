package sum25.studentcode.backend.modules.Lesson.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonSummary {

    private Long lessonId;
    private String lessonTitle;
    private String lessonType;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private String createdByUsername;
}
