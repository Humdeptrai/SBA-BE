package sum25.studentcode.backend.modules.Lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.LessonCollection;

public interface LessonCollectionRepository extends JpaRepository<LessonCollection, Long> {
}