package sum25.studentcode.backend.modules.StudentPractice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;

import java.util.List;

@Repository
public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Long> {

    boolean existsByPracticeSessionAndStudent(PracticeSession session, User student);

    @Query("""
        SELECT sp FROM StudentPractice sp
        JOIN FETCH sp.practiceSession ps
        WHERE sp.status = :status
    """)
    List<StudentPractice> findWithSessionByStatus(StudentPractice.PracticeStatus status);
}
