package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.authentication.AuthenticationControllerDto;
import team.anonyms.converter.dto.controller.authentication.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.authentication.LoginResultControllerDto;
import team.anonyms.converter.dto.controller.authentication.PasswordResetControllerDto;
import team.anonyms.converter.dto.service.authentication.AuthenticationServiceDto;
import team.anonyms.converter.dto.service.authentication.CredentialsServiceDto;
import team.anonyms.converter.dto.service.authentication.LoginResultServiceDto;
import team.anonyms.converter.dto.service.authentication.PasswordResetServiceDto;

@Component
public class AuthenticationMapper {
    public CredentialsServiceDto credentialsControllerDtoToService(CredentialsControllerDto credentialsControllerDto) {
        return new CredentialsServiceDto(
                credentialsControllerDto.email(),
                credentialsControllerDto.password()
        );
    }

    public PasswordResetServiceDto passwordResetControllerDtoToService(
            PasswordResetControllerDto passwordResetControllerDto
    ) {
        return new PasswordResetServiceDto(
                passwordResetControllerDto.email(),
                passwordResetControllerDto.verificationCode(),
                passwordResetControllerDto.newPassword()
        );
    }

    public LoginResultControllerDto loginResultServiceDtoToController(LoginResultServiceDto loginResultServiceDto) {
        return new LoginResultControllerDto(
                loginResultServiceDto.success(),
                loginResultServiceDto.userId(),
                loginResultServiceDto.username(),
                loginResultServiceDto.email(),
                loginResultServiceDto.isVerified()
        );
    }

    public AuthenticationControllerDto authenticationServiceDtoToControllerDto(
            AuthenticationServiceDto authenticationServiceDto
    ) {
        return new AuthenticationControllerDto(
                loginResultServiceDtoToController(authenticationServiceDto.result()),
                authenticationServiceDto.jwtToken()
        );
    }
}
