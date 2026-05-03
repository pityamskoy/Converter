package team.anonyms.converter.repositories;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.Pattern;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, UUID> {
    List<Pattern> findAllByUserId(UUID userId);
    void deleteAllByUserId(UUID userId);

    /**
     * <p>
     *     Finds a pattern by its ID.
     * </p>
     *
     * @param id - an ID of a pattern.
     *
     * @return pattern, which has been found by {@code id}.
     * Please, note that returning pattern is null if provided {@code id} is null.
     *
     * @throws EntityNotFoundException if pattern is not found.
     */
    default @Nullable Pattern findPatternById(@Nullable UUID id) {
        if (id == null) {
            return null;
        }

        Optional<Pattern> patternOptional = findById(id);
        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + id);
        }

        return patternOptional.get();
    }
}
