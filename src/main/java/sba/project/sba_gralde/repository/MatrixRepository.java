package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.Matrix;

@Repository
public interface MatrixRepository extends JpaRepository<Matrix, Long> {
}