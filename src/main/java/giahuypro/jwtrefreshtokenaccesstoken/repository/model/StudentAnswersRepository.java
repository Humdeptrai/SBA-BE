package giahuypro.jwtrefreshtokenaccesstoken.repository.model;

import giahuypro.jwtrefreshtokenaccesstoken.model.StudentAnswers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswersRepository extends JpaRepository<StudentAnswers, Long> {
}