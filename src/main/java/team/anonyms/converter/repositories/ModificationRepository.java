package team.anonyms.converter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.anonyms.converter.entities.Modification;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, UUID> {
    List<Modification> findAllByPatternId(UUID patternId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Modification m WHERE m.pattern.id = :patternId")
    void deleteAllByPatternId(UUID patternId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Modification m WHERE m.pattern.user.id = :userId")
    void deleteAllByUserId(UUID userId);

    long countAllByPatternId(UUID patternId);
}
