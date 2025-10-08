package sum25.studentcode.backend.modules.Subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}