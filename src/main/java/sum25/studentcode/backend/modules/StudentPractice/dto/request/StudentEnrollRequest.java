package sum25.studentcode.backend.modules.StudentPractice.dto.request;

import lombok.Data;

@Data
public class StudentEnrollRequest {
    private Long sessionId;     // ✅ xác định buổi cụ thể
    private String sessionCode; // vẫn giữ để xác thực
}
