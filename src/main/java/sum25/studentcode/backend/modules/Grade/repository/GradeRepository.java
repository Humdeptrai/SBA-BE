package sum25.studentcode.backend.modules.Grade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Grade;

import java.util.Arrays;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByCreatedBy_UserId(Long createdByUserId);
}