package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Định nghĩa tỉ lệ phân bổ câu hỏi và điểm theo từng mức độ nhận thức
 *
 * VD: Ma trận Tiếng Anh lớp 10
 * - Nhận biết: 30% (3 câu, 3 điểm)
 * - Thông hiểu: 50% (5 câu, 5 điểm)
 * - Vận dụng: 20% (2 câu, 2 điểm)
 *
 * @author Teacher System
 */
@Entity
@Table(name = "matrix_allocations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"matrix_id", "knowledge_level"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "allocationId")
@ToString(exclude = {"matrix"})
public class MatrixAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matrix_id", nullable = false, referencedColumnName = "matrix_id")
    private Matrix matrix;

    /**
     * Mức độ nhận thức: RECALL, UNDERSTAND, APPLY, ANALYZE
     * Tương ứng: Nhận biết, Thông hiểu, Vận dụng, Vận dụng cao
     */
    @Column(name = "knowledge_level", nullable = false, length = 20)
    private String knowledgeLevel;  // Enum: RECALL, UNDERSTAND, APPLY, ANALYZE

    /**
     * Tỉ lệ phần trăm (30, 50, 20, ...)
     * Tổng tất cả allocation của 1 matrix = 100%
     */
    @Column(name = "percent_allocation", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentAllocation;

    /**
     * Số lượng câu hỏi được phân bổ cho mức độ này
     * VD: Nhận biết 30% × 10 câu = 3 câu
     */
    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    /**
     * Tổng điểm được phân bổ
     * VD: Nhận biết 30% × 10 điểm = 3 điểm
     */
    @Column(name = "marks_allocated", precision = 19, scale = 4, nullable = false)
    private BigDecimal marksAllocated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
