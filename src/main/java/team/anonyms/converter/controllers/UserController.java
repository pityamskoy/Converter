package team.anonyms.converter.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.user.UserCreatedControllerDto;
import team.anonyms.converter.dto.controller.user.UserToCreateControllerDto;
import team.anonyms.converter.entities.User;
import team.anonyms.converter.mappers.controller.UserControllerMapper;
import team.anonyms.converter.services.UserService;

@SuppressWarnings(value = {"unused"})
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/users")
public final class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserControllerMapper userControllerMapper;

    @GetMapping
    public ResponseEntity<User> a() {
        log.info("Called getUsers");
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/create")
    public ResponseEntity<UserCreatedControllerDto> register(@RequestBody UserToCreateControllerDto userToCreateControllerDto) {
        log.info("Called register; userToCreateControllerDto={}", userToCreateControllerDto);

        return userContollerMapper.
    }
}
