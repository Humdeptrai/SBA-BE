package sum25.studentcode.backend.modules.Lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByLessonTitle(String lessonTitle);
}
