package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "matrix_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "matrixQuestionId")
@ToString(exclude = {"matrix", "question", "createdBy"})
public class MatrixQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matrix_question_id")
    private Long matrixQuestionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matrix_id", referencedColumnName = "matrix_id")
    private Matrix matrix;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private Questions question;
    
    @Column(name = "marks_allocated", precision = 19, scale = 4)
    private BigDecimal marksAllocated;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
