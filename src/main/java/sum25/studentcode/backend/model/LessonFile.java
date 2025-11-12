package sum25.studentcode.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String lessonId;
    private String fileName;
    private String fileType;
    @Lob
    @JdbcTypeCode(Types.BINARY) // Hoặc Types.VARBINARY
    @Column(name = "data")
    private byte[] data;


    @Column(name = "display_order")
    private Integer displayOrder;
}
