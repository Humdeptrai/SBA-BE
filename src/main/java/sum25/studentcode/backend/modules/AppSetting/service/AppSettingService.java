package sum25.studentcode.backend.modules.AppSetting.service;

import sum25.studentcode.backend.modules.AppSetting.dto.request.AppSettingRequest;
import sum25.studentcode.backend.modules.AppSetting.dto.response.AppSettingResponse;

import java.util.List;

public interface AppSettingService {
    AppSettingResponse createAppSetting(AppSettingRequest request);
    AppSettingResponse getAppSettingById(Long id);
    List<AppSettingResponse> getAllAppSettings();
    AppSettingResponse updateAppSetting(Long id, AppSettingRequest request);
    void deleteAppSetting(Long id);
}