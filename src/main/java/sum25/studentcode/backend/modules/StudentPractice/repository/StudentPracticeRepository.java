package sum25.studentcode.backend.modules.StudentPractice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Long> {
    boolean existsByPracticeSessionAndStudent(PracticeSession session, User student);

    List<StudentPractice> findByStatusAndPracticeSession_EndTimeBefore(
            StudentPractice.PracticeStatus status, LocalDateTime endTime);

}