package sum25.studentcode.backend.modules.AppSetting.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppSettingResponse {
    private Long settingId;
    private String settingName;
    private String settingValue;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}