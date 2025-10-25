package sum25.studentcode.backend.modules.Level.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelResponse {

    private Long levelId;
    private String levelName;
    private Integer difficultyScore;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
