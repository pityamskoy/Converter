package team.anonyms.converter.controllers.frontend;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.authentication.AuthenticationControllerDto;
import team.anonyms.converter.dto.controller.authentication.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.*;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.mappers.AuthenticationMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.UserService;

import java.util.UUID;

import static team.anonyms.converter.controllers.frontend.AuthenticationController.createJwtCookie;

@RestController
@RequestMapping("/users")
@SuppressWarnings(value = {"DataFlowIssue"})
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationMapper authenticationMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper,
            AuthenticationMapper authenticationMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationMapper = authenticationMapper;
    }

    @PostMapping
    public ResponseEntity<LoginResultControllerDto> register(
            @RequestBody UserToRegisterControllerDto userToRegister,
            HttpServletResponse response
    ) {
        logger.info("Called register; username={}", userToRegister.username());

        AuthenticationControllerDto result = authenticationMapper.authenticationServiceDtoToControllerDto(
                userService.register(userMapper.userToRegisterControllerDtoToService(userToRegister))
        );

        response.addHeader(HttpHeaders.SET_COOKIE, createJwtCookie(result.jwtToken(), 14400).toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(result.result());
    }

    @PutMapping
    public ResponseEntity<UserControllerDto> updateUser(@RequestBody UserToUpdateControllerDto userToUpdate) {
        logger.info("Called updateUser");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserServiceDto userUpdated = userService.updateUser(
                userMapper.userToUpdateControllerDtoToService(userToUpdate),
                userId
        );

        return ResponseEntity.ok(userMapper.userServiceDtoToControllerDto(userUpdated));
    }

    @PutMapping("/email")
    public ResponseEntity<UserControllerDto> updateEmail(@RequestBody String email) {
        logger.info("Called updateEmail");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserServiceDto userUpdated = userService.updateEmail(email, userId);

        return ResponseEntity.ok(userMapper.userServiceDtoToControllerDto(userUpdated));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletResponse response) {
        logger.info("Called deleteUser");

        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.deleteUser(userId);

        response.addHeader(HttpHeaders.SET_COOKIE, createJwtCookie("", 0).toString());
        return ResponseEntity.noContent().build();
    }
}
