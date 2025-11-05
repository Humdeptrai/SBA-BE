package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_practice_id", "question_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "answerId")
@ToString(exclude = {"studentPractice", "question"})
public class StudentAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practice_id", referencedColumnName = "practice_id")
    private StudentPractice studentPractice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private Questions question;
    
    @Column(name = "selected_option_id")
    private Long selectedOptionId;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "marks_earned", precision = 19, scale = 4)
    private BigDecimal marksEarned;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
}
