package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToUpdateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToUpdateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;

@Component
public class ModificationMapper {
    public ModificationToCreateServiceDto modificationToCreateControllerDtoToService(
            ModificationToCreateControllerDto modificationToCreateControllerDto
    ) {
        return new ModificationToCreateServiceDto(
                modificationToCreateControllerDto.oldName(),
                modificationToCreateControllerDto.newName(),
                modificationToCreateControllerDto.newValue(),
                modificationToCreateControllerDto.newType()
        );
    }

    public ModificationToUpdateServiceDto modificationToUpdateControllerDtoToService(
            ModificationToUpdateControllerDto modificationToUpdateControllerDto
    ) {
        return new ModificationToUpdateServiceDto(
                modificationToUpdateControllerDto.id(),
                modificationToUpdateControllerDto.oldName(),
                modificationToUpdateControllerDto.newName(),
                modificationToUpdateControllerDto.newValue(),
                modificationToUpdateControllerDto.newType()
        );
    }

    public ModificationControllerDto modificationServiceDtoToControllerDto(
            ModificationServiceDto modificationServiceDto
    ) {
        return new ModificationControllerDto(
                modificationServiceDto.id(),
                modificationServiceDto.oldName(),
                modificationServiceDto.newName(),
                modificationServiceDto.newValue(),
                modificationServiceDto.newType()
        );
    }

    public Modification modificationToCreateServiceDtoToEntity(
            ModificationToCreateServiceDto modificationToCreateServiceDto,
            Pattern pattern
    ) {
        return Modification.builder()
                .oldName(modificationToCreateServiceDto.oldName())
                .newName(modificationToCreateServiceDto.newName())
                .newValue(modificationToCreateServiceDto.newValue())
                .newType(modificationToCreateServiceDto.newType())
                .pattern(pattern)
                .build();
    }

    public Modification modificationToUpdateServiceDtoToEntity(
            ModificationToUpdateServiceDto modificationToUpdateServiceDto,
            Pattern pattern
    ) {
        return new Modification(
                modificationToUpdateServiceDto.id(),
                modificationToUpdateServiceDto.oldName(),
                modificationToUpdateServiceDto.newName(),
                modificationToUpdateServiceDto.newValue(),
                modificationToUpdateServiceDto.newType(),
                pattern
        );
    }

    public ModificationServiceDto modificationToServiceDto(Modification modification) {
        return new ModificationServiceDto(
                modification.getId(),
                modification.getOldName(),
                modification.getNewName(),
                modification.getNewValue(),
                modification.getNewType()
        );
    }
}
