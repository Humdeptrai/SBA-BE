package sum25.studentcode.backend.modules.TeacherMatrix;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.TeacherMatrix.dto.request.TeacherMatrixRequest;
import sum25.studentcode.backend.modules.TeacherMatrix.dto.response.TeacherMatrixResponse;
import sum25.studentcode.backend.modules.TeacherMatrix.service.TeacherMatrixService;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-matrices")
@RequiredArgsConstructor
public class TeacherMatrixController {

    private final TeacherMatrixService teacherMatrixService;

    @PostMapping
    public TeacherMatrixResponse createTeacherMatrix(@RequestBody TeacherMatrixRequest request) {
        return teacherMatrixService.createTeacherMatrix(request);
    }

    @GetMapping("/{id}")
    public TeacherMatrixResponse getTeacherMatrixById(@PathVariable Long id) {
        return teacherMatrixService.getTeacherMatrixById(id);
    }

    @GetMapping
    public List<TeacherMatrixResponse> getAllTeacherMatrices() {
        return teacherMatrixService.getAllTeacherMatrices();
    }

    @PutMapping("/{id}")
    public TeacherMatrixResponse updateTeacherMatrix(@PathVariable Long id, @RequestBody TeacherMatrixRequest request) {
        return teacherMatrixService.updateTeacherMatrix(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTeacherMatrix(@PathVariable Long id) {
        teacherMatrixService.deleteTeacherMatrix(id);
    }
}