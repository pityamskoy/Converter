package team.anonyms.converter.services.frontend;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PatternRepository patternRepository;
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatternMapper patternMapper;
    @Autowired
    private ModificationMapper modificationMapper;

    public List<PatternServiceDto> getAllPatternsByUserId(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + userId);
        }

        return userOptional.get().getPatterns().stream().map(patternMapper::patternToServiceDto).toList();
    }

    public PatternServiceDto createPattern(PatternToCreateServiceDto patternToCreate) {
        Optional<User> userOptional = userRepository.findById(patternToCreate.userId());

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found; id=" + patternToCreate.userId());
        }

        Pattern patternCreated = patternMapper.patternToCreateServiceDtoToEntity(patternToCreate);
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
        patternUpdated.setConversionType(patternToUpdate.conversionType());

        modificationRepository.deleteAll(patternUpdated.getModifications());
        patternUpdated.setModifications(modifications);

        patternRepository.save(patternUpdated);

        return patternMapper.patternToServiceDto(patternUpdated);
    }

    public void deletePattern(UUID patternId) {
        Optional<Pattern> patternOptional = patternRepository.findById(patternId);

        if (patternOptional.isEmpty()) {
            throw new EntityNotFoundException("Pattern not found; id=" + patternId);
        }

        patternRepository.delete(patternOptional.get());
    }
}
