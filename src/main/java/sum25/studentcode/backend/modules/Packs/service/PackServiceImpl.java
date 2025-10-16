package sum25.studentcode.backend.modules.Packs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.modules.Packs.dto.request.PackRequest;
import sum25.studentcode.backend.modules.Packs.dto.response.PackResponse;
import sum25.studentcode.backend.modules.Packs.repository.PackRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackServiceImpl implements PackService {

    private final PackRepository packRepository;

    private Pack findPackOrThrow(Long packId) {
        return packRepository.findById(packId)
                .orElseThrow(() -> new RuntimeException("Pack not found with ID: " + packId));
    }

    @Override
    public List<PackResponse> getAllPacks() {
        // Trả về tất cả các gói (dùng cho Admin quản lý)
        return packRepository.findAll().stream()
                .map(PackResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackResponse> getAllActivePacks() {
        // Lấy tất cả các gói có isActive = true (dùng cho người dùng cuối)
        return packRepository.findByIsActiveTrue().stream()
                .map(PackResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PackResponse> getPackById(Long packId) {
        return packRepository.findById(packId)
                .map(PackResponse::fromEntity);
    }

    @Override
    @Transactional
    public PackResponse createPack(PackRequest request) {
        log.info("Creating new pack: {}", request.getName());
        Pack pack = Pack.builder()
                .name(request.getName())
                .description(request.getDescription())
                .packValue(request.getPackValue())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .isActive(true) // Mặc định là Active khi tạo
                .build();

        pack = packRepository.save(pack);
        return PackResponse.fromEntity(pack);
    }

    @Override
    @Transactional
    public PackResponse updatePack(Long packId, PackRequest request) {
        Pack pack = findPackOrThrow(packId);
        log.info("Updating pack ID: {}", packId);

        // Cập nhật các trường
        pack.setName(request.getName());
        pack.setDescription(request.getDescription());
        pack.setPackValue(request.getPackValue());

        if (request.getCurrency() != null) {
            pack.setCurrency(request.getCurrency());
        }

        // Chỉ cập nhật isActive nếu được truyền vào request
        if (request.getIsActive() != null) {
            pack.setIsActive(request.getIsActive());
        }

        pack = packRepository.save(pack);
        return PackResponse.fromEntity(pack);
    }

    @Override
    @Transactional
    public void deletePack(Long packId) {
        Pack pack = findPackOrThrow(packId);

        // Vô hiệu hóa (Soft Delete) gói
        if (pack.getIsActive()) {
            pack.setIsActive(false);
            packRepository.save(pack);
            log.warn("Pack ID {} has been deactivated (soft deleted).", packId);
        } else {
            log.warn("Pack ID {} is already inactive.", packId);
        }
    }
}
