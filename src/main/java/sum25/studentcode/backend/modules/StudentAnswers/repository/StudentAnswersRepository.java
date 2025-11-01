package sum25.studentcode.backend.modules.StudentAnswers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Questions;
import sum25.studentcode.backend.model.StudentAnswers;
import sum25.studentcode.backend.model.StudentPractice;

import java.util.List;
import java.util.Optional;

public interface StudentAnswersRepository extends JpaRepository<StudentAnswers, Long> {
    List<StudentAnswers> findByStudentPractice_PracticeId(Long practiceId);
    // ✅ Tìm đáp án theo practice và question (để update draft)
    Optional<StudentAnswers> findByStudentPracticeAndQuestion(StudentPractice practice, Questions question);

    // ✅ Lấy tất cả đáp án của 1 practice
    List<StudentAnswers> findByStudentPractice(StudentPractice practice);
}