package team.anonyms.converter.controllers.frontend;

import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.user.UserControllerDto;
import team.anonyms.converter.dto.controller.user.UserToRegisterControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateControllerDto;
import team.anonyms.converter.dto.controller.user.UserToUpdateEmailControllerDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;
import team.anonyms.converter.dto.service.user.UserServiceDto;
import team.anonyms.converter.mappers.CredentialsMapper;
import team.anonyms.converter.mappers.UserMapper;
import team.anonyms.converter.services.frontend.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final CredentialsMapper credentialsMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper,
            CredentialsMapper credentialsMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.credentialsMapper = credentialsMapper;
    }

    @PostMapping
    public ResponseEntity<LoginResultControllerDto> register(
            @RequestBody UserToRegisterControllerDto userToRegister,
            HttpServletResponse response
    ) {
        logger.info("Called register; username={}", userToRegister.username());

        Pair<LoginResultServiceDto, String> result = userService.register(
                userMapper.userToRegisterControllerDtoToService(userToRegister)
        );

        ResponseCookie responseCookie = ResponseCookie.from("jwtToken", result.b)
                .path("/")
                .maxAge(14400)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credentialsMapper.loginResultServiceDtoToController(result.a));
    }

    @PutMapping
    public ResponseEntity<UserControllerDto> updateUser(@RequestBody UserToUpdateControllerDto userToUpdate) {
        logger.info("Called updateUser; userId={}", userToUpdate.id());

        UserServiceDto userUpdated = userService.updateUser(
                userMapper.userToUpdateControllerDtoToService(userToUpdate)
        );

        return ResponseEntity.ok(userMapper.userServiceDtoToControllerDto(userUpdated));
    }

    @PutMapping("/email")
    public ResponseEntity<UserControllerDto> updateEmail(
            @RequestBody UserToUpdateEmailControllerDto userToUpdateEmail
    ) {
        logger.info("Called updateEmail; userId={}", userToUpdateEmail.id());

        UserServiceDto userUpdated = userService.updateEmail(
                userMapper.userToUpdateEmailControllerDtoToService(userToUpdateEmail)
        );

        return ResponseEntity.ok(userMapper.userServiceDtoToControllerDto(userUpdated));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        logger.info("Called deleteUser; id={}", userId.toString());

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
