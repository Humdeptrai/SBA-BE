package giahuypro.jwtrefreshtokenaccesstoken.repository.model;

import giahuypro.jwtrefreshtokenaccesstoken.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Long> {
}