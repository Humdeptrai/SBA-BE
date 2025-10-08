package sum25.studentcode.backend.modules.AppSetting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.AppSetting;
import sum25.studentcode.backend.modules.AppSetting.dto.request.AppSettingRequest;
import sum25.studentcode.backend.modules.AppSetting.dto.response.AppSettingResponse;
import sum25.studentcode.backend.modules.AppSetting.repository.AppSettingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppSettingServiceImpl implements AppSettingService {

    private final AppSettingRepository appSettingRepository;

    @Override
    public AppSettingResponse createAppSetting(AppSettingRequest request) {
        AppSetting appSetting = AppSetting.builder()
                .settingName(request.getSettingName())
                .settingValue(request.getSettingValue())
                .description(request.getDescription())
                .build();
        appSetting = appSettingRepository.save(appSetting);
        return convertToResponse(appSetting);
    }

    @Override
    public AppSettingResponse getAppSettingById(Long id) {
        AppSetting appSetting = appSettingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppSetting not found"));
        return convertToResponse(appSetting);
    }

    @Override
    public List<AppSettingResponse> getAllAppSettings() {
        return appSettingRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppSettingResponse updateAppSetting(Long id, AppSettingRequest request) {
        AppSetting appSetting = appSettingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppSetting not found"));
        appSetting.setSettingName(request.getSettingName());
        appSetting.setSettingValue(request.getSettingValue());
        appSetting.setDescription(request.getDescription());
        appSetting = appSettingRepository.save(appSetting);
        return convertToResponse(appSetting);
    }

    @Override
    public void deleteAppSetting(Long id) {
        if (!appSettingRepository.existsById(id)) {
            throw new RuntimeException("AppSetting not found");
        }
        appSettingRepository.deleteById(id);
    }

    private AppSettingResponse convertToResponse(AppSetting appSetting) {
        AppSettingResponse response = new AppSettingResponse();
        response.setSettingId(appSetting.getSettingId());
        response.setSettingName(appSetting.getSettingName());
        response.setSettingValue(appSetting.getSettingValue());
        response.setDescription(appSetting.getDescription());
        response.setCreatedAt(appSetting.getCreatedAt());
        response.setUpdatedAt(appSetting.getUpdatedAt());
        return response;
    }
}