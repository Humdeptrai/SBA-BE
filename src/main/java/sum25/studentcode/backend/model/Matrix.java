package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matrixs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "matrixId")
@ToString(exclude = {"matrixQuestions", "exam"})
public class Matrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matrix_id")
    private Long matrixId;
    
    @Column(name = "matrix_name")
    private String matrixName;
    
    @Lob
    private String description;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "matrix", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatrixQuestion> matrixQuestions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @OneToMany(mappedBy = "matrix")
    private List<PracticeSession> practiceSessions;

}
