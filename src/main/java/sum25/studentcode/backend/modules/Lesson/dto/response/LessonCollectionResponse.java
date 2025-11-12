package sum25.studentcode.backend.modules.Lesson.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCollectionResponse {
    private Long collectionId;
    private String collectionName;
    private String description;
    private String createdByUsername;
    private List<LessonSummary> lessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}