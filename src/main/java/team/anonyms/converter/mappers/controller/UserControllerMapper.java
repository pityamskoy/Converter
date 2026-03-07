package team.anonyms.converter.mappers.controller;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.user.UserToCreateControllerDto;
import team.anonyms.converter.dto.service.user.UserToCreateServiceDto;

@Component
public final class UserControllerMapper {
    private UserToCreateServiceDto userToCreateToService(UserToCreateControllerDto userToCreateControllerDto) {
        return new UserToCreateServiceDto(
                userToCreateControllerDto.username(),
                userToCreateControllerDto.email(),
                userToCreateControllerDto.password()
        );
    }
}
