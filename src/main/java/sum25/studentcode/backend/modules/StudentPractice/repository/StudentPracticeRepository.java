package sum25.studentcode.backend.modules.StudentPractice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Long> {
    boolean existsByPracticeSessionAndStudent(PracticeSession session, User student);

    @Query("SELECT sp FROM StudentPractice sp " +
            "JOIN FETCH sp.practiceSession ps " +
            "JOIN FETCH ps.matrix m " +
            "JOIN FETCH m.exam e " +
            "WHERE sp.status = :status")
    List<StudentPractice> findWithSessionAndExamByStatus(@Param("status") StudentPractice.PracticeStatus status);


}