package sum25.studentcode.backend.modules.PracticeSession.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PracticeSessionRequest {
    private Long lessonId;
    private Long matrixId;              // Gắn với Matrix (bộ câu hỏi)
    private String sessionCode;         // Mã đề thi / mã buổi
    private String sessionName;         // Tên buổi thi
    private String description;         // Mô tả thêm (tùy chọn)     // Bật/tắt
    private Integer maxParticipants;    // Giới hạn người tham gia
    private LocalDateTime examDate;     // Ngày giờ thi
    private Integer durationMinutes;    // Thời lượng thi
}
