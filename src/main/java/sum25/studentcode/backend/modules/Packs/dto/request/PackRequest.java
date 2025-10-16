package sum25.studentcode.backend.modules.Packs.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackRequest {
    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá trị gói không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị gói phải lớn hơn 0")
    private BigDecimal packValue;

    // Loại tiền tệ (ví dụ: VND)
    private String currency = "VND";

    // Trạng thái hoạt động (cho việc cập nhật)
    private Boolean isActive;
}
