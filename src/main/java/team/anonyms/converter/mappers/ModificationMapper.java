package team.anonyms.converter.mappers;

import org.springframework.stereotype.Component;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.entities.Modification;

import java.util.UUID;

@Component
public final class ModificationMapper {
    public ModificationServiceDto modificationControllerDtoToServiceDto(
            ModificationControllerDto modificationControllerDto
    ) {
        return new ModificationServiceDto(
                modificationControllerDto.id(),
                modificationControllerDto.oldName(),
                modificationControllerDto.newName(),
                modificationControllerDto.newType(),
                modificationControllerDto.newValue()
        );
    }

    public ModificationToCreateServiceDto modificationToCreateControllerDtoToService(
            ModificationToCreateControllerDto modificationToCreateControllerDto
    ) {
        return new ModificationToCreateServiceDto(
                modificationToCreateControllerDto.oldName(),
                modificationToCreateControllerDto.newName(),
                modificationToCreateControllerDto.newType(),
                modificationToCreateControllerDto.newValue()
        );
    }

    public ModificationControllerDto modificationServiceDtoToControllerDto(
            ModificationServiceDto modificationServiceDto
    ) {
        return new ModificationControllerDto(
                modificationServiceDto.id(),
                modificationServiceDto.oldName(),
                modificationServiceDto.newName(),
                modificationServiceDto.newType(),
                modificationServiceDto.newValue()
        );
    }

    public Modification modificationServiceDtoToEntity(ModificationServiceDto modificationServiceDto) {
        return new Modification(
                modificationServiceDto.id(),
                modificationServiceDto.oldName(),
                modificationServiceDto.newName(),
                modificationServiceDto.newType(),
                modificationServiceDto.newValue()
        );
    }

    public Modification modificationToCreateServiceDtoToEntity(
            ModificationToCreateServiceDto modificationToCreateServiceDto
    ) {
        return new Modification(
                UUID.randomUUID(),
                modificationToCreateServiceDto.oldName(),
                modificationToCreateServiceDto.newName(),
                modificationToCreateServiceDto.newType(),
                modificationToCreateServiceDto.newValue()
        );
    }

    public ModificationServiceDto modificationToServiceDto(Modification modification) {
        return new ModificationServiceDto(
                modification.getId(),
                modification.getOldName(),
                modification.getNewName(),
                modification.getNewType(),
                modification.getNewValue()
        );
    }
}
