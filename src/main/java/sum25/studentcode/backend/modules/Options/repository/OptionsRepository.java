package sum25.studentcode.backend.modules.Options.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.Options;

public interface OptionsRepository extends JpaRepository<Options, Long> {
}