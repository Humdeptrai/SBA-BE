package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collectionId;
    
    private String collectionName; // "Unit 1: My School", "Chủ đề: Present Tense"
    private String description;
    
    @ManyToOne
    private User createdBy; // Giáo viên sở hữu
    
    @ManyToMany
    private List<Lesson> lessons; // Các lesson trong bộ sưu tập
}
