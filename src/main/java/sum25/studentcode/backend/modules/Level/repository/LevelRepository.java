package sum25.studentcode.backend.modules.Level.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
}