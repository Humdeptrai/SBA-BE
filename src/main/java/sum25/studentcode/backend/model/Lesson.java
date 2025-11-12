package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "lessonId")
@ToString(exclude = {"grade", "createdBy"})
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

    @Column(name = "lesson_type")
    private String lessonType;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "methodology")
    private String methodology;

    @Column(name = "materials")
    private String materials;

    @Column(name = "homework")
    private String homework;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false; // Học viên có thấy không?

    @Column(name = "thumbnail_url")
    private String thumbnailUrl; //Link url image

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "lessons", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LessonCollection> collections = new ArrayList<>();

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<Questions> questions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User createdBy;

}
