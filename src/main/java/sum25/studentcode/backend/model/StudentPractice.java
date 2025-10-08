package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_practice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "practiceId")
@ToString(exclude = {"studentAnswers", "practiceSession", "student"})
public class StudentPractice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "practice_id")
    private Long practiceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    private PracticeSession practiceSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id")
    private User student;
    
    @Column(name = "per_time")
    private LocalDateTime perTime;
    
    @Column(name = "submit_time")
    private LocalDateTime submitTime;
    
    @Column(name = "total_score", precision = 19, scale = 4)
    private BigDecimal totalScore;
    
    @Column
    private String status;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "studentPractice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StudentAnswers> studentAnswers = new ArrayList<>();
}
