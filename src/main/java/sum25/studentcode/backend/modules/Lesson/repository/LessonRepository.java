package sum25.studentcode.backend.modules.Lesson.repository;

import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Grade;
import sum25.studentcode.backend.model.Lesson;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.Lesson.dto.response.LessonGradeResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByLessonTitle(String lessonTitle);

    Optional<Lesson> findByLessonTitle(String lessonTitle);

    Optional<Lesson> findByLessonTitleIgnoreCase(String lessonTitle);

    List<Lesson> findAllByCreatedBy_UserId(Long createdByUserId);


    List<Lesson> findAllByGrade_GradeId(Long gradeGradeId);
}
