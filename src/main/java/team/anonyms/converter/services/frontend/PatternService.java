package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.repositories.ModificationRepository;
import team.anonyms.converter.repositories.PatternRepository;
import team.anonyms.converter.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public final class PatternService {
    private final PatternRepository patternRepository;
    private final ModificationRepository modificationRepository;
    private final UserRepository userRepository;
    private final PatternMapper patternMapper;
    private final ModificationMapper modificationMapper;

    public PatternService(
            PatternRepository patternRepository,
            ModificationRepository modificationRepository,
            UserRepository userRepository,
            PatternMapper patternMapper,
            ModificationMapper modificationMapper
    ) {
        this.patternRepository = patternRepository;
        this.modificationRepository = modificationRepository;
        this.userRepository = userRepository;
        this.patternMapper = patternMapper;
        this.modificationMapper = modificationMapper;
    }

    public List<PatternServiceDto> getAllPatternsByUserId(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        return userOptional.get().getPatterns().stream().map(patternMapper::patternToServiceDto).toList();
    }

    /**
     * <p>
     *     This method finds patterns by its IDs.
     * </p>
     *
     * @param id - an ID of a pattern.
     *
     * @return pattern, which has been found by {@code id}. Please, note that returning pattern is null if provided {@code id} is null.
     *
     * @throws EntityNotFoundException if pattern is not found.
     */
    public @Nullable Pattern findPatternById(@Nullable UUID id) {
        if (id == null) {
            return null;
        }

        Optional<Pattern> patternOptional = patternRepository.findById(id);

        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + id);
        }

        return patternOptional.get();
    }

    public PatternServiceDto createPattern(PatternToCreateServiceDto patternToCreate) {
        Optional<User> userOptional = userRepository.findById(patternToCreate.userId());

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + patternToCreate.userId());
        }

        Pattern patternCreated = patternMapper.patternToCreateServiceDtoToEntity(patternToCreate);

        for (Modification modification: patternCreated.getModifications()) {
            modificationRepository.save(modification);
        }

        patternRepository.save(patternCreated);

        User user = userOptional.get();
        List<Pattern> patterns = user.getPatterns();
        patterns.add(patternCreated);

        user.setPatterns(patterns);
        userRepository.save(user);

        return patternMapper.patternToServiceDto(patternCreated);
    }

    public PatternServiceDto updatePattern(PatternServiceDto patternToUpdate) {
        Optional<Pattern> pattern = patternRepository.findById(patternToUpdate.id());

        if (pattern.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternToUpdate.id());
        }

        List<Modification> modifications = patternToUpdate.modifications().stream().
                map(modificationMapper::modificationServiceDtoToEntity).toList();

        Pattern patternUpdated = pattern.get();
        patternUpdated.setName(patternToUpdate.name());

        modificationRepository.deleteAll(patternUpdated.getModifications());
        patternUpdated.setModifications(modifications);

        patternRepository.save(patternUpdated);

        return patternMapper.patternToServiceDto(patternUpdated);
    }

    public void deletePattern(UUID patternId) {
        Optional<Pattern> patternOptional = patternRepository.findById(patternId);
        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; patternId=" + patternId);
        }

        List<User> users = userRepository.findAll();

        User userForDeletion = null;
        for (User user : users) {
            List<Pattern> patterns = user.getPatterns();

            for (Pattern pattern : patterns) {
                if (pattern.getId().equals(patternId)) {
                    userForDeletion = user;
                    break;
                }
            }
        }

        if (userForDeletion == null) {
            throw new EntityNotFoundException("User not found by pattern; patternId=" + patternId);
        }

        userForDeletion.setPatterns(userForDeletion.getPatterns().stream().filter(p -> !p.getId().equals(patternId)).toList());

        userRepository.save(userForDeletion);
        patternRepository.delete(patternOptional.get());
    }
}
