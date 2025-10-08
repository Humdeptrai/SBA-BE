package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}