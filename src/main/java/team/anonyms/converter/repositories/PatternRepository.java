package team.anonyms.converter.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.anonyms.converter.entities.Pattern;

import java.util.UUID;

@Repository
public interface PatternRepository extends JpaRepository<@NonNull Pattern, @NonNull UUID> {
}
