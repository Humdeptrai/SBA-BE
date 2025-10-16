package sum25.studentcode.backend.modules.Packs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.modules.Packs.dto.request.PackRequest;
import sum25.studentcode.backend.modules.Packs.dto.response.PackResponse;
import sum25.studentcode.backend.modules.Packs.service.PackService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
@Slf4j
public class PackController {
    private final PackService packService;

    // --- API Dành cho User (Public) ---

    /**
     * GET /api/packs
     * Lấy danh sách các gói Credit đang hoạt động (dùng cho trang mua sắm)
     */
    @GetMapping
    public ResponseEntity<List<PackResponse>> getAllActivePacks() {
        log.info("Request to get all active packs.");
        List<PackResponse> activePacks = packService.getAllActivePacks();

        if (activePacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(activePacks);
    }

    // --- API Dành cho Admin (Quản lý) ---

    /**
     * GET /api/packs/admin - Cần phân quyền Admin
     * Lấy tất cả các gói (bao gồm cả inactive)
     */
    @GetMapping("/admin")
    public ResponseEntity<List<PackResponse>> getAllPacksForAdmin() {
        log.info("Admin request to get all packs.");
        List<PackResponse> allPacks = packService.getAllPacks();
        return ResponseEntity.ok(allPacks);
    }

    /**
     * GET /api/packs/{id}
     * Lấy chi tiết một gói theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<PackResponse>> getPackById(@PathVariable Long id) {
        log.info("Request to get pack by ID: {}", id);
        Optional<PackResponse> pack = packService.getPackById(id);
        return ResponseEntity.ok(pack);
    }

    /**
     * POST /api/packs - Cần phân quyền Admin
     * Tạo một gói Pack mới
     */
    @PostMapping
    public ResponseEntity<PackResponse> createPack(@RequestBody PackRequest request) {
        log.info("Admin request to create new pack: {}", request.getName());
        PackResponse newPack = packService.createPack(request);
        return new ResponseEntity<>(newPack, HttpStatus.CREATED);
    }

    /**
     * PUT /api/packs/{id} - Cần phân quyền Admin
     * Cập nhật thông tin gói Pack
     */
    @PutMapping("/{id}")
    public ResponseEntity<PackResponse> updatePack(@PathVariable Long id, @RequestBody PackRequest request) {
        log.info("Admin request to update pack ID: {}", id);
        PackResponse updatedPack = packService.updatePack(id, request);
        return ResponseEntity.ok(updatedPack);
    }

    /**
     * DELETE /api/packs/{id} - Cần phân quyền Admin
     * Vô hiệu hóa (Deactivate) gói Pack
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePack(@PathVariable Long id) {
        log.warn("Admin request to deactivate pack ID: {}", id);
        packService.deletePack(id);
        return ResponseEntity.noContent().build();
    }


}
