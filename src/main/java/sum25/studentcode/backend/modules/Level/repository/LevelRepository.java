package sum25.studentcode.backend.modules.Level.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    boolean existsByLevelName(String levelName);
}
