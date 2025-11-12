package sum25.studentcode.backend.modules.StudentPractice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.PracticeSession;
import sum25.studentcode.backend.model.StudentPractice;
import sum25.studentcode.backend.model.User;
import sum25.studentcode.backend.modules.StudentPractice.dto.response.StudentPracticeResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPracticeRepository extends JpaRepository<StudentPractice, Long> {

    boolean existsByPracticeSessionAndStudent(PracticeSession session, User student);

    @Query("""
        SELECT sp FROM StudentPractice sp
        JOIN FETCH sp.practiceSession ps
        WHERE sp.status = :status
    """)
    List<StudentPractice> findWithSessionByStatus(StudentPractice.PracticeStatus status);

    // StudentPracticeRepository.java
    Optional<StudentPractice> findTopByPracticeSessionAndStudentOrderByAttemptNumberDesc(PracticeSession practiceSession, User student);


    List<StudentPractice> findAllByStudent_UserId(Long studentUserId);

    List<StudentPractice> findByStatus(StudentPractice.PracticeStatus status, Sort sort);
}
