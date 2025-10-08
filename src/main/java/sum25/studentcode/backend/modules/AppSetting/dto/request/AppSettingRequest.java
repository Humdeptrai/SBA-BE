package sum25.studentcode.backend.modules.AppSetting.dto.request;

import lombok.Data;

@Data
public class AppSettingRequest {
    private String settingName;
    private String settingValue;
    private String description;
}