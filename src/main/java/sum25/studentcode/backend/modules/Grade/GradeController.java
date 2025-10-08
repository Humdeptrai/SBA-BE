package sum25.studentcode.backend.modules.Grade;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Grade.dto.request.GradeRequest;
import sum25.studentcode.backend.modules.Grade.dto.response.GradeResponse;
import sum25.studentcode.backend.modules.Grade.service.GradeService;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    public GradeResponse createGrade(@RequestBody GradeRequest request) {
        return gradeService.createGrade(request);
    }

    @GetMapping("/{id}")
    public GradeResponse getGradeById(@PathVariable Long id) {
        return gradeService.getGradeById(id);
    }

    @GetMapping
    public List<GradeResponse> getAllGrades() {
        return gradeService.getAllGrades();
    }

    @PutMapping("/{id}")
    public GradeResponse updateGrade(@PathVariable Long id, @RequestBody GradeRequest request) {
        return gradeService.updateGrade(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }
}