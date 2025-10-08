package sum25.studentcode.backend.modules.Subject.dto.request;

import lombok.Data;

@Data
public class SubjectRequest {
    private String subjectName;
    private String subjectCode;
    private Integer creditId;
    private String syllabus;
}