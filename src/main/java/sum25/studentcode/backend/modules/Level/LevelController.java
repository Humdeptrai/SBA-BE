package sum25.studentcode.backend.modules.Level;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Level.dto.request.LevelRequest;
import sum25.studentcode.backend.modules.Level.dto.response.LevelResponse;
import sum25.studentcode.backend.modules.Level.service.LevelService;

import java.util.List;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    public LevelResponse createLevel(@RequestBody LevelRequest request) {
        return levelService.createLevel(request);
    }

    @GetMapping("/{id}")
    public LevelResponse getLevelById(@PathVariable Long id) {
        return levelService.getLevelById(id);
    }

    @GetMapping
    public List<LevelResponse> getAllLevels() {
        return levelService.getAllLevels();
    }

    @PutMapping("/{id}")
    public LevelResponse updateLevel(@PathVariable Long id, @RequestBody LevelRequest request) {
        return levelService.updateLevel(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteLevel(@PathVariable Long id) {
        levelService.deleteLevel(id);
    }
}