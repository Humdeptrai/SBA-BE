package sum25.studentcode.backend.modules.AppSetting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.AppSetting.dto.request.AppSettingRequest;
import sum25.studentcode.backend.modules.AppSetting.dto.response.AppSettingResponse;
import sum25.studentcode.backend.modules.AppSetting.service.AppSettingService;

import java.util.List;

@RestController
@RequestMapping("/api/app-settings")
@RequiredArgsConstructor
@Tag(name = "App Settings", description = "Quản lý cấu hình hệ thống (App Settings)")
public class AppSettingController {

    private final AppSettingService appSettingService;

    @GetMapping("/test")
    @Operation(summary = "Test controller", description = "Kiểm tra xem AppSettingController có hoạt động không")
    public String test() {
        return "AppSettingController OK!";
    }

    @PostMapping
    @Operation(summary = "Tạo AppSetting", description = "Tạo mới một cấu hình ứng dụng trong hệ thống")
    @ApiResponse(responseCode = "200", description = "Tạo thành công")
    public AppSettingResponse createAppSetting(@RequestBody AppSettingRequest request) {
        return appSettingService.createAppSetting(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy AppSetting theo ID", description = "Truy vấn chi tiết cấu hình theo ID")
    @ApiResponse(responseCode = "200", description = "Trả về chi tiết cấu hình")
    public AppSettingResponse getAppSettingById(@PathVariable Long id) {
        return appSettingService.getAppSettingById(id);
    }

    @GetMapping
    @Operation(summary = "Danh sách tất cả AppSettings", description = "Lấy danh sách tất cả cấu hình hệ thống")
    @ApiResponse(responseCode = "200", description = "Trả về danh sách cấu hình")
    public List<AppSettingResponse> getAllAppSettings() {
        return appSettingService.getAllAppSettings();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật AppSetting", description = "Cập nhật thông tin cấu hình theo ID")
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    public AppSettingResponse updateAppSetting(@PathVariable Long id, @RequestBody AppSettingRequest request) {
        return appSettingService.updateAppSetting(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa AppSetting", description = "Xóa cấu hình hệ thống theo ID")
    @ApiResponse(responseCode = "204", description = "Xóa thành công")
    public void deleteAppSetting(@PathVariable Long id) {
        appSettingService.deleteAppSetting(id);
    }
}
