package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "questionId")
@ToString(exclude = {"options", "studentAnswers", "matrixQuestions", "level", "questionType", "subject", "lesson"})
public class Questions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", referencedColumnName = "lesson_id")
    private Lesson lesson;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id", referencedColumnName = "question_type_id")
    private QuestionType questionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", referencedColumnName = "level_id")
    private Level level;
    
    @Lob
    @Column(name = "question_text")
    private String questionText;
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @Lob
    private String explanation;

    @Column(name = "is_active")
    private Boolean isActive = true;


    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Options> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<StudentAnswers> studentAnswers = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<MatrixQuestion> matrixQuestions = new ArrayList<>();
}
