package sum25.studentcode.backend.modules.Lesson.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCollectionRequest {
    private String collectionName;
    private String description;
    private List<Long> lessonIds; // IDs of lessons to include in the collection
}