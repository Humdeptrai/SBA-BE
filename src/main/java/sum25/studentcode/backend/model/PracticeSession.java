package sum25.studentcode.backend.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", referencedColumnName = "exam_id")
    private Exam exam;

    @Column(name = "session_code", unique = true)
    private String sessionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")
    private User teacher;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "max_participants")
    private Integer maxParticipants;

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

}
