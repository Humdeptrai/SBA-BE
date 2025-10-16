package sum25.studentcode.backend.modules.Packs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.studentcode.backend.model.Pack;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
    List<Pack> findByIsActiveTrue();

    Optional<Pack> findById(Long id);
}
