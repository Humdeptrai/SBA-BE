package sum25.studentcode.backend.modules.Grade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public GradeResponse createGrade(@RequestBody GradeRequest request) {
        return gradeService.createGrade(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT',  'ADMIN')")
    public GradeResponse getGradeById(@PathVariable Long id) {
        return gradeService.getGradeById(id);
    }

    @GetMapping("/by/user/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT' ,  'ADMIN')")
    public List<GradeResponse> getAllGrades(@PathVariable Long userId) {
        return gradeService.getAllGrades(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public GradeResponse updateGrade(@PathVariable Long id, @RequestBody GradeRequest request) {
        return gradeService.updateGrade(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public void deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }
}