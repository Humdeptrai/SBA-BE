package sum25.studentcode.backend.modules.QuestionType.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.QuestionType;

import java.util.Optional;

@Repository
public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {
    boolean existsByTypeName(String typeName);

    Optional<Object> findByTypeNameIgnoreCase(String typeName);
}
