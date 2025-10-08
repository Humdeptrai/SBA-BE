package sum25.studentcode.backend.modules.Level.dto.request;

import lombok.Data;

@Data
public class LevelRequest {
    private String levelName;
    private Integer difficultyScore;
    private String description;
}