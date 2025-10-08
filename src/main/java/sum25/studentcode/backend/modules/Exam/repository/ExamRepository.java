package sum25.studentcode.backend.modules.Exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}