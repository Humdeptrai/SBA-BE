package sum25.studentcode.backend.modules.Syllabus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusRequest {
    private String title;
    private String description;
    private Long subjectId;
    private Long gradeId;
    private Long createdBy_UserId;
}