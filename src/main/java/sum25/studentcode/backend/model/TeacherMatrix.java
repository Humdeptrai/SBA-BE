package sum25.studentcode.backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_matrixs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "teacherMatrixId")
@ToString(exclude = {"teacher", "matrix"})
public class TeacherMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_matrix_id")
    private Long teacherMatrixId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_id")
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matrix_id", referencedColumnName = "matrix_id")
    private Matrix matrix;

    @Column(precision = 19, scale = 4)
    private BigDecimal grade;

    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
