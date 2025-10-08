package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.MatrixQuestion;

@Repository
public interface MatrixQuestionRepository extends JpaRepository<MatrixQuestion, Long> {
}