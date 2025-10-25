package sum25.studentcode.backend.modules.Level.service;

import sum25.studentcode.backend.modules.Level.dto.request.LevelRequest;
import sum25.studentcode.backend.modules.Level.dto.response.LevelResponse;

import java.util.List;

public interface LevelService {

    LevelResponse createLevel(LevelRequest request);

    LevelResponse updateLevel(Long id, LevelRequest request);

    void deleteLevel(Long id);

    LevelResponse getLevelById(Long id);

    List<LevelResponse> getAllLevels();
}
