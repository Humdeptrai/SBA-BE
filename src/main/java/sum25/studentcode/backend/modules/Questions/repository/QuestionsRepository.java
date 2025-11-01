package sum25.studentcode.backend.modules.Questions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Questions;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    List<Questions> findByLesson_LessonId(Long lessonId);
}