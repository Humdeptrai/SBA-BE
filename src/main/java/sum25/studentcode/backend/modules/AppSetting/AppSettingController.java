package sum25.studentcode.backend.modules.AppSetting;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.AppSetting.dto.request.AppSettingRequest;
import sum25.studentcode.backend.modules.AppSetting.dto.response.AppSettingResponse;
import sum25.studentcode.backend.modules.AppSetting.service.AppSettingService;

import java.util.List;

@RestController
@RequestMapping("/api/app-settings")
@RequiredArgsConstructor
public class AppSettingController {

    private final AppSettingService appSettingService;

    @PostMapping
    public AppSettingResponse createAppSetting(@RequestBody AppSettingRequest request) {
        return appSettingService.createAppSetting(request);
    }

    @GetMapping("/{id}")
    public AppSettingResponse getAppSettingById(@PathVariable Long id) {
        return appSettingService.getAppSettingById(id);
    }

    @GetMapping
    public List<AppSettingResponse> getAllAppSettings() {
        return appSettingService.getAllAppSettings();
    }

    @PutMapping("/{id}")
    public AppSettingResponse updateAppSetting(@PathVariable Long id, @RequestBody AppSettingRequest request) {
        return appSettingService.updateAppSetting(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteAppSetting(@PathVariable Long id) {
        appSettingService.deleteAppSetting(id);
    }
}