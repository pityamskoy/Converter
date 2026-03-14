package team.anonyms.converter.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.UserService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/users")
public final class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PutMapping("/update")
    public ResponseEntity<UserControllerDto> updateUser(@RequestBody UserToUpdateControllerDto userToUpdate) {
        log.info("Called updateUser; userToUpdate={}", userToUpdate);

        UserServiceDto userUpdated = userService.updateUser(
                userMapper.userToUpdateControllerDtoToService(userToUpdate));

        return ResponseEntity.ok(userMapper.userServiceDtoToControllerDto(userUpdated));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestBody UUID userId) {
        log.info("Called deleteUser; id={}", userId.toString());

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
