package sum25.studentcode.backend.modules.Level.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelRequest {

    @NotBlank(message = "Level name is required")
    private String levelName;

    @NotNull(message = "Difficulty score is required")
    private Double difficultyScore;

    private String description;
}
