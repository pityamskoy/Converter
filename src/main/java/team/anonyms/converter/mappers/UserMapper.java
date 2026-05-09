package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateEmailControllerDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.dto.service.user.UserToRegisterServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateEmailServiceDto;
import team.anonyms.converter.dto.service.user.UserToUpdateServiceDto;
import team.anonyms.converter.entities.User;

@Component
public class UserMapper {
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
        return new UserToUpdateServiceDto(
                userToUpdateControllerDto.id(),
                userToUpdateControllerDto.username(),
                userToUpdateControllerDto.password()
        );
    }

    public UserToUpdateEmailServiceDto userToUpdateEmailControllerDtoToService(
            UserToUpdateEmailControllerDto userToUpdateEmailControllerDto
    ) {
        return new UserToUpdateEmailServiceDto(
                userToUpdateEmailControllerDto.id(),
                userToUpdateEmailControllerDto.email()
        );
    }

    public UserControllerDto userServiceDtoToControllerDto(UserServiceDto userServiceDto) {
        return new UserControllerDto(
                userServiceDto.id(),
                userServiceDto.username(),
                userServiceDto.email(),
                userServiceDto.isVerified()
        );
    }

    public User userToRegisterServiceDtoToEntity(UserToRegisterServiceDto userToRegisterServiceDto) {
        return User.builder()
                .username(userToRegisterServiceDto.username())
                .email(userToRegisterServiceDto.email())
                .password(userToRegisterServiceDto.password())
                .isVerified(false)
                .build();
    }

    public UserServiceDto userToServiceDto(User user) {
        return new UserServiceDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getIsVerified()
        );
    }
}
