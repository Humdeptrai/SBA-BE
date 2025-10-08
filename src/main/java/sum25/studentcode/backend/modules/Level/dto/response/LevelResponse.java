package sum25.studentcode.backend.modules.Level.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LevelResponse {
    private Long levelId;
    private String levelName;
    private Integer difficultyScore;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}