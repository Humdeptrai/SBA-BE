package sum25.studentcode.backend.modules.Matrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Matrix;

import java.util.List;

public interface MatrixRepository extends JpaRepository<Matrix, Long> {
    List<Matrix> findAllByCreatedBy_UserId(Long createdByUserId);
}
