package team.anonyms.converter.mappers.service;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.service.user.UserToCreateServiceDto;
import team.anonyms.converter.entities.User;

import java.util.UUID;

@Component
public final class UserServiceMapper {
    public User userToCreateToEntity(UserToCreateServiceDto userToCreateServiceDto) {
        return new User(
                UUID.randomUUID(),

        );
    }
}
