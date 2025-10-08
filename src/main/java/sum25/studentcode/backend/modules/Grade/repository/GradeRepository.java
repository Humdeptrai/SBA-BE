package sum25.studentcode.backend.modules.Grade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}