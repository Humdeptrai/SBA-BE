package sum25.studentcode.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "practice_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "sessionId")
@ToString(exclude = {"studentPractices", "exam", "student", "teacher"})
public class PracticeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "description")
    private String description;

    @Column(name = "session_code")
    private String sessionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")
    private User teacher;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "is_active")
    private Boolean isActive=true;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // ✅ Thêm JsonFormat cho examDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @Column(name = "exam_date")
    private LocalDateTime examDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "practiceSession", fetch = FetchType.LAZY)
    private List<StudentPractice> studentPractices = new ArrayList<>();

    /** ✅ Quan hệ mới: mỗi session gắn với 1 matrix */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matrix_id", nullable = false)
    private Matrix matrix;

    @Column(name = "current_participants")
    private Integer currentParticipants = 0; // đếm người đã tham gia

    @Column(name = "auto_close")
    private Boolean autoClose = true; // cho phép tự động đóng khi hết giờ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
