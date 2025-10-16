package sum25.studentcode.backend.modules.Packs.service;

import sum25.studentcode.backend.model.Pack;
import sum25.studentcode.backend.modules.Packs.dto.request.PackRequest;
import sum25.studentcode.backend.modules.Packs.dto.response.PackResponse;

import java.util.List;
import java.util.Optional;

// Giả định interface này tồn tại
public interface PackService {
    /**
     * Lấy tất cả các gói Credit.
     * @return Danh sách tất cả các Pack
     */
    List<PackResponse> getAllPacks();

    /**
     * Lấy tất cả các gói Credit đang hoạt động.
     * @return Danh sách các Pack đang active
     */
    List<PackResponse> getAllActivePacks();

    /**
     * Tìm Pack theo ID.
     * @param packId ID của gói
     * @return Optional<Pack>
     */
    Optional<PackResponse> getPackById(Long packId);

    /**
     * Tạo mới một gói Credit.
     * @param pack Thông tin gói cần tạo
     * @return Gói đã được tạo
     */
    PackResponse createPack(PackRequest pack);

    /**
     * Cập nhật thông tin một gói Credit.
     * @param packId ID của gói cần cập nhật
     * @param updatedPack Thông tin gói đã được cập nhật
     * @return Gói đã được cập nhật
     */
    PackResponse updatePack(Long packId, PackRequest updatedPack);

    /**
     * Xóa một gói Credit theo ID.
     * @param packId ID của gói cần xóa
     */
    void deletePack(Long packId);
}
