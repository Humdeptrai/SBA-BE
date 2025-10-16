package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "lessonId")
@ToString(exclude = {"grade"})
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", referencedColumnName = "grade_id")
    private Grade grade;
    
    @Column(name = "lesson_title")
    private String lessonTitle;
    
    @Lob
    @Column(name = "lesson_content")
    private String lessonContent;
    
    @Lob
    @Column(name = "lesson_objectives")
    private String lessonObjectives;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
}
