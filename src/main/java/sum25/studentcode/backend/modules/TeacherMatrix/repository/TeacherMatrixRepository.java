package sum25.studentcode.backend.modules.TeacherMatrix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.TeacherMatrix;

public interface TeacherMatrixRepository extends JpaRepository<TeacherMatrix, Long> {
}