package sum25.studentcode.backend.modules.Matrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.MatrixAllocation;
import sum25.studentcode.backend.modules.Matrix.dto.response.MatrixResponse;

public interface MatrixAllocateRepository extends JpaRepository<MatrixAllocation, Long> {
}
