package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "questionTypeId")
@ToString(exclude = "questions")
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_type_id")
    private Long questionTypeId;
    
    @Column(name = "type_name", unique = true)
    private String typeName;
    
    @Lob
    private String description;
    
    @Column(name = "enabled_at")
    private Boolean enabledAt;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "questionType", fetch = FetchType.LAZY)
    private List<Questions> questions = new ArrayList<>();
}
