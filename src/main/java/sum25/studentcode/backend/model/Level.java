package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "levelId")
@ToString(exclude = "questions")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private Long levelId;
    
    @Column(name = "level_name", unique = true)
    private String levelName;
    
    @Column(name = "difficulty_score")
    private Double difficultyScore;
    
    @Lob
    private String description;


    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "level", fetch = FetchType.LAZY)
    private List<Questions> questions = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
}

