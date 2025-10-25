package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "examId")
@ToString(exclude = {"subject", "practiceSessions"})
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "exam_name")
    private String examName;

    @Column(name = "exam_code", nullable = false, unique = true, length = 50)
    private String examCode;

    @Lob
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "exam_date")
    private LocalDateTime examDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY)
    private List<PracticeSession> practiceSessions = new ArrayList<>();


}
