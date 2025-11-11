package sum25.studentcode.backend.modules.Syllabus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.modules.Syllabus.entity.Syllabus;

import java.util.List;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    List<Syllabus> findByCreatedBy_UserId(Long createdBy_UserId);
}