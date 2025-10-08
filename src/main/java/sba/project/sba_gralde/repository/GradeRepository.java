package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByGradeLevel(String gradeLevel);
}