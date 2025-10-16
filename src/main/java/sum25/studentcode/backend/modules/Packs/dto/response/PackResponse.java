package sum25.studentcode.backend.modules.Packs.dto.response;

import lombok.Builder;
import lombok.Data;
import sum25.studentcode.backend.model.Pack;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PackResponse {
    private Long packId;
    private String name;
    private String description;
    private BigDecimal packValue;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Phương thức static để ánh xạ từ Entity sang DTO
    public static PackResponse fromEntity(Pack pack) {
        return PackResponse.builder()
                .packId(pack.getPackId())
                .name(pack.getName())
                .description(pack.getDescription())
                .packValue(pack.getPackValue())
                .currency(pack.getCurrency())
                .isActive(pack.getIsActive())
                .createdAt(pack.getCreatedAt())
                .build();
    }
}
