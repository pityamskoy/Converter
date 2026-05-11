package team.anonyms.converter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.Modification;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, UUID> {
    List<Modification> findAllByPatternId(UUID patternId);

    void deleteAllByPatternId(UUID patternId);

    long countAllByPatternId(UUID patternId);
}
