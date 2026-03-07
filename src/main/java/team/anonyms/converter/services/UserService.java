package team.anonyms.converter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.anonyms.converter.dto.service.user.UserCreatedServiceDto;
import team.anonyms.converter.dto.service.user.UserToCreateServiceDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.repositories.UserRepository;

import java.util.List;

@Service
public final class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserCreatedServiceDto register(UserToCreateServiceDto userToCreateServiceDto) {

    }

}
