package sum25.studentcode.backend.modules.Level.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sum25.studentcode.backend.model.Level;
import sum25.studentcode.backend.modules.Level.dto.request.LevelRequest;
import sum25.studentcode.backend.modules.Level.dto.response.LevelResponse;
import sum25.studentcode.backend.modules.Level.repository.LevelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    @Override
    public LevelResponse createLevel(LevelRequest request) {
        Level level = Level.builder()
                .levelName(request.getLevelName())
                .difficultyScore(request.getDifficultyScore())
                .description(request.getDescription())
                .build();
        level = levelRepository.save(level);
        return convertToResponse(level);
    }

    @Override
    public LevelResponse getLevelById(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));
        return convertToResponse(level);
    }

    @Override
    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LevelResponse updateLevel(Long id, LevelRequest request) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));
        level.setLevelName(request.getLevelName());
        level.setDifficultyScore(request.getDifficultyScore());
        level.setDescription(request.getDescription());
        level = levelRepository.save(level);
        return convertToResponse(level);
    }

    @Override
    public void deleteLevel(Long id) {
        if (!levelRepository.existsById(id)) {
            throw new RuntimeException("Level not found");
        }
        levelRepository.deleteById(id);
    }

    private LevelResponse convertToResponse(Level level) {
        LevelResponse response = new LevelResponse();
        response.setLevelId(level.getLevelId());
        response.setLevelName(level.getLevelName());
        response.setDifficultyScore(level.getDifficultyScore());
        response.setDescription(level.getDescription());
        response.setCreatedAt(level.getCreatedAt());
        response.setUpdatedAt(level.getUpdatedAt());
        return response;
    }
}