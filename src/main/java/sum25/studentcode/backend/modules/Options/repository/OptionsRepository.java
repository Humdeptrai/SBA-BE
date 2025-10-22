package sum25.studentcode.backend.modules.Options.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Options;
import sum25.studentcode.backend.model.Questions;

import java.util.List;

public interface OptionsRepository extends JpaRepository<Options, Long> {
    List<Options> findByQuestion_QuestionId(Long questionId);
    boolean existsByQuestionAndOptionTextIgnoreCase(Questions question, String optionText);
    boolean existsByQuestionAndOptionOrder(Questions question, Integer optionOrder);
    boolean existsByQuestionAndOptionTextIgnoreCaseAndOptionIdNot(Questions question, String optionText, Long optionId);
    boolean existsByQuestionAndOptionOrderAndOptionIdNot(Questions question, Integer optionOrder, Long optionId);

}