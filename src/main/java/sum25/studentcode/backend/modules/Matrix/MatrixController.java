package sum25.studentcode.backend.modules.Matrix;

import lombok.RequiredArgsConstructor;
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
    public MatrixResponse createMatrix(@RequestBody MatrixRequest request) {
        return matrixService.createMatrix(request);
    }

    @GetMapping("/{id}")
    public MatrixResponse getMatrixById(@PathVariable Long id) {
        return matrixService.getMatrixById(id);
    }

    @GetMapping
    public List<MatrixResponse> getAllMatrices() {
        return matrixService.getAllMatrices();
    }

    @PutMapping("/{id}")
    public MatrixResponse updateMatrix(@PathVariable Long id, @RequestBody MatrixRequest request) {
        return matrixService.updateMatrix(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMatrix(@PathVariable Long id) {
        matrixService.deleteMatrix(id);
    }
}