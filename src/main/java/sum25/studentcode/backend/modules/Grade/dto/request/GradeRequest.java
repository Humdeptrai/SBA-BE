package sum25.studentcode.backend.modules.Grade.dto.request;

import lombok.Data;

@Data
public class GradeRequest {
    private String gradeLevel;
    private String description;
}