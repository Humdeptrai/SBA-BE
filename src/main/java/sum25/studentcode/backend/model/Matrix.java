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
@Table(name = "matrixs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "matrixId")
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

    @Column(name = "total_marks", precision = 19, scale = 4, nullable = false)
    private BigDecimal totalMarks;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "matrix", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MatrixQuestion> matrixQuestions = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;


    @OneToMany(mappedBy = "matrix", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatrixAllocation> matrixAllocations = new ArrayList<>();

    @OneToMany(mappedBy = "matrix")
    private List<PracticeSession> practiceSessions = new ArrayList<>();;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User createdBy;
}
