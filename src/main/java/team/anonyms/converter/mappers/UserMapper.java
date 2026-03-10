package team.anonyms.converter.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public final class UserMapper {
    @Autowired
    private PatternMapper patternMapper;

    public UserToRegisterServiceDto userToRegisterControllerDtoToService(
            UserToRegisterControllerDto userToRegisterControllerDto
    ) {
        return new UserToRegisterServiceDto(
                userToRegisterControllerDto.username(),
                userToRegisterControllerDto.email(),
                userToRegisterControllerDto.password()
        );
    }

    public UserToUpdateServiceDto userToUpdateControllerDtoToService(
            UserToUpdateControllerDto userToUpdateControllerDto
    ) {
        List<PatternServiceDto> patterns = userToUpdateControllerDto.patterns().stream().
                map(patternMapper::patternControllerDtoToServiceDto).toList();

        return new UserToUpdateServiceDto(
                userToUpdateControllerDto.id(),
                userToUpdateControllerDto.username(),
                userToUpdateControllerDto.email(),
                userToUpdateControllerDto.password(),
                patterns
        );
    }

    public UserControllerDto userServiceDtoToControllerDto(UserServiceDto userServiceDto) {
        List<PatternControllerDto> patterns = userServiceDto.patterns().stream().
                map(patternMapper::patternServiceDtoToControllerDto).toList();

        return new UserControllerDto(
                userServiceDto.id(),
                userServiceDto.username(),
                userServiceDto.email(),
                patterns
        );
    }

    public User userToRegisterServiceDtoToEntity(UserToRegisterServiceDto userToRegisterServiceDto) {
        return new User(
                UUID.randomUUID(),
                userToRegisterServiceDto.username(),
                userToRegisterServiceDto.email(),
                userToRegisterServiceDto.password(),
                new ArrayList<>()
        );
    }

    public UserServiceDto userToServiceDto(User user) {
        List<PatternServiceDto> patterns = user.getPatterns().stream().map(patternMapper::patternToServiceDto).toList();

        return new UserServiceDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                patterns
        );
    }
}
