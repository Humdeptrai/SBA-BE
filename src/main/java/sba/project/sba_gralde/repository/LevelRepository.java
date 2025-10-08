package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    boolean existsByLevelName(String levelName);
}