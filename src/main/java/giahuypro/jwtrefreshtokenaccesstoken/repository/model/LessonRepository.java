package giahuypro.jwtrefreshtokenaccesstoken.repository.model;

import giahuypro.jwtrefreshtokenaccesstoken.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}