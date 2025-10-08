package sum25.studentcode.backend.modules.StudentPractice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.StudentPractice;

public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Long> {
}