package sum25.studentcode.backend.modules.QuestionType.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.QuestionType;

public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {
}