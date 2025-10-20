package sum25.studentcode.backend.modules.Options.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Options;

import java.util.List;

public interface OptionsRepository extends JpaRepository<Options, Long> {
    List<Options> findByQuestion_QuestionId(Long questionId);
}