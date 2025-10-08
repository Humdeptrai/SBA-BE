package giahuypro.jwtrefreshtokenaccesstoken.repository.model;

import giahuypro.jwtrefreshtokenaccesstoken.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}