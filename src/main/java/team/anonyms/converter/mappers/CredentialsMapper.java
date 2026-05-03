package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;

@Component
public class CredentialsMapper {
    public CredentialsServiceDto credentialsControllerDtoToService(CredentialsControllerDto credentialsControllerDto) {
        return new CredentialsServiceDto(
                credentialsControllerDto.email(),
                credentialsControllerDto.password(),
                credentialsControllerDto.jwtToken()
        );
    }

    public LoginResultControllerDto loginResultServiceDtoToController(LoginResultServiceDto loginResultServiceDto) {
        return new LoginResultControllerDto(
                loginResultServiceDto.success(),
                loginResultServiceDto.username(),
                loginResultServiceDto.email(),
                loginResultServiceDto.userId(),
                loginResultServiceDto.jwtToken()
        );
    }
}
