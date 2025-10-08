package sum25.studentcode.backend.modules.Lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}