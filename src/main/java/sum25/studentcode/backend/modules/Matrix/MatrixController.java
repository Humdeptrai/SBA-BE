package sum25.studentcode.backend.modules.Matrix;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Matrix.dto.request.MatrixRequest;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;
import sum25.studentcode.backend.modules.Matrix.service.MatrixService;

import java.util.List;

@RestController
@RequestMapping("/api/matrices")
@RequiredArgsConstructor
public class MatrixController {

    private final MatrixService matrixService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public MatrixResponse createMatrix(@RequestBody MatrixRequest request) {
        return matrixService.createMatrix(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public MatrixResponse getMatrixById(@PathVariable Long id) {
        return matrixService.getMatrixById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public List<MatrixResponse> getAllMatrices() {
        return matrixService.getAllMatrices();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public MatrixResponse updateMatrix(@PathVariable Long id, @RequestBody MatrixRequest request) {
        return matrixService.updateMatrix(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteMatrix(@PathVariable Long id) {
        matrixService.deleteMatrix(id);
    }
}