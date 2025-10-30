package sum25.studentcode.backend.modules.Exam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRequest {
    private String examName;
    private String examCode;
    private String description;
    private Integer durationMinutes;

    // ✅ Đảm bảo nhận đúng timezone từ frontend
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime examDate;

    private Long lessonId;
}