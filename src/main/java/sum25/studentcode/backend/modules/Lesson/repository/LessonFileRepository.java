package sum25.studentcode.backend.modules.Lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.LessonFile;

import java.util.List;

@Repository
public interface LessonFileRepository extends JpaRepository<LessonFile, String> {
    List<LessonFile> findByLessonId(String lessonId);
}
