package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "packId")
public class Pack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pack_id")
    private Long packId;

    @Column(nullable = false, unique = true)
    private String name; // Tên gói (ví dụ: "Gói 100 Credit")

    @Column
    private String description;

    // Giá trị chung cho cả Giá tiền và Số Credit nhận được (vì 1 Credit = 1 đơn vị tiền)
    // Sẽ là giá trị tiền thật (VD: 100.000) và cũng là số Credit được nhận (VD: 100.000)
    @Column(name = "pack_value", precision = 19, scale = 4, nullable = false)
    private BigDecimal packValue;

    // Loại tiền tệ (ví dụ: VND)
    @Column(length = 3)
    private String currency = "VND";

    // Gói có đang hoạt động không
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}