package sum25.studentcode.backend.modules.Syllabus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusResponse {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private Long gradeId;
    private Long createdBy_UserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}