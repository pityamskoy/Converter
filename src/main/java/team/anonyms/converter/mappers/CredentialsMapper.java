package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.credentials.CredentialsControllerDto;
import team.anonyms.converter.dto.controller.credentials.LoginResultControllerDto;
import team.anonyms.converter.dto.service.credentials.CredentialsServiceDto;
import team.anonyms.converter.dto.service.credentials.LoginResultServiceDto;

@Component
public final class CredentialsMapper {
    public CredentialsServiceDto credentialsControllerDtoToService(CredentialsControllerDto credentialsControllerDto) {
        return new CredentialsServiceDto(
                credentialsControllerDto.email(),
                credentialsControllerDto.password()
        );
    }

    public LoginResultControllerDto loginResultServiceDtoToController(LoginResultServiceDto loginResultServiceDto) {
        return new LoginResultControllerDto(
                loginResultServiceDto.success(),
                loginResultServiceDto.userId()
        );
    }
}
