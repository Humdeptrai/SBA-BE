package sum25.studentcode.backend.modules.Lesson.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    private String lessonTitle;

    private String lessonContent;

    private String lessonObjectives;

    private Long gradeId; // optional
}
