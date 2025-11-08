package sum25.studentcode.backend.modules.Questions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Questions;

import java.util.Collection;
import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    List<Questions> findByLesson_LessonId(Long lessonId);
    Page<Questions> findByLevel_LevelName(String levelName, Pageable pageable);
    Page<Questions> findByLevel_LevelNameAndQuestionIdNotIn(String levelName, List<Long> questionIds, Pageable pageable);

    Page<Questions> findByLevel_LevelNameAndLesson_LessonId(String levelLevelName, Long lessonLessonId, Pageable pageable);

    Page<Questions> findByLevel_LevelNameAndLesson_LessonIdAndQuestionIdNotIn(String levelLevelName, Long lessonLessonId, List<Long> questionIds, Pageable pageable);
}