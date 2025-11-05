package sum25.studentcode.backend.modules.Lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Lesson;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByLessonTitle(String lessonTitle);

    Optional<Lesson> findByLessonTitle(String lessonTitle);

    Optional<Lesson> findByLessonTitleIgnoreCase(String lessonTitle);
}
