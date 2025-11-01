package sum25.studentcode.backend.modules.Level.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.studentcode.backend.core.exception.ApiException;
import sum25.studentcode.backend.model.Level;
import sum25.studentcode.backend.modules.Level.dto.request.LevelRequest;
import sum25.studentcode.backend.modules.Level.dto.response.LevelResponse;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    @Override
    public LevelResponse createLevel(LevelRequest request) {
        // ✅ Kiểm tra trùng tên
        if (levelRepository.existsByLevelName(request.getLevelName())) {
            throw new ApiException(
                    "LEVEL_DUPLICATE",
                    "Tên cấp độ \"" + request.getLevelName() + "\" đã tồn tại.",
                    400
            );
        }

        // ✅ Kiểm tra điểm hợp lệ
        validateDifficultyScore(request.getDifficultyScore());

        Level level = Level.builder()
                .levelName(request.getLevelName())
                .difficultyScore(request.getDifficultyScore())
                .description(request.getDescription())
                .build();

        levelRepository.save(level);
        return toResponse(level);
    }

    @Override
    public LevelResponse updateLevel(Long id, LevelRequest request) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "LEVEL_NOT_FOUND",
                        "Không tìm thấy cấp độ cần cập nhật.",
                        404
                ));

        // ✅ Kiểm tra trùng tên với Level khác
        if (levelRepository.existsByLevelName(request.getLevelName()) &&
                !level.getLevelName().equalsIgnoreCase(request.getLevelName())) {
            throw new ApiException(
                    "LEVEL_DUPLICATE",
                    "Tên cấp độ \"" + request.getLevelName() + "\" đã được sử dụng.",
                    400
            );
        }

        // ✅ Kiểm tra điểm hợp lệ
        validateDifficultyScore(request.getDifficultyScore());

        level.setLevelName(request.getLevelName());
        level.setDifficultyScore(request.getDifficultyScore());
        level.setDescription(request.getDescription());

        levelRepository.save(level);
        return toResponse(level);
    }

    @Override
    public void deleteLevel(Long id) {
        if (!levelRepository.existsById(id)) {
            throw new ApiException("LEVEL_NOT_FOUND", "Không tìm thấy cấp độ cần xóa.", 404);
        }
        levelRepository.deleteById(id);
    }

    @Override
    public LevelResponse getLevelById(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ApiException("LEVEL_NOT_FOUND", "Không tìm thấy cấp độ.", 404));
        return toResponse(level);
    }

    @Override
    public List<LevelResponse> getAllLevels() {
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            throw new ApiException("LEVEL_EMPTY", "Chưa có cấp độ nào được tạo.", 404);
        }
        return levels.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void validateDifficultyScore(Double score) {
        if (score == null || score < 0.01 || score > 100.0) {
            throw new ApiException(
                    "INVALID_DIFFICULTY_SCORE",
                    "Điểm độ khó phải nằm trong khoảng 0.01 – 100.0.",
                    400
            );
        }
    }

    private LevelResponse toResponse(Level level) {
        return LevelResponse.builder()
                .levelId(level.getLevelId())
                .levelName(level.getLevelName())
                .difficultyScore(level.getDifficultyScore())
                .description(level.getDescription())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }
}
