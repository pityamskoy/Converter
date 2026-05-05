package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToUpdateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToUpdateServiceDto;
import team.anonyms.converter.entities.Modification;
import team.anonyms.converter.entities.Pattern;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ModificationMapperTest {

    private final ModificationMapper mapper = new ModificationMapper();

    @Test
    void testModificationToCreateControllerDtoToService() {
        ModificationToCreateControllerDto controllerDto = new ModificationToCreateControllerDto(
                "old",
                "new",
                "value",
                "type"
        );

        ModificationToCreateServiceDto serviceDto = mapper.modificationToCreateControllerDtoToService(controllerDto);

        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("value", serviceDto.newValue());
        assertEquals("type", serviceDto.newType());
    }

    @Test
    void testModificationToUpdateControllerDtoToService() {
        UUID id = UUID.randomUUID();
        ModificationToUpdateControllerDto controllerDto = new ModificationToUpdateControllerDto(
                id,
                "old",
                "new",
                "value",
                "type"
        );

        ModificationToUpdateServiceDto serviceDto = mapper.modificationToUpdateControllerDtoToService(controllerDto);

        assertEquals(id, serviceDto.id());
        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("value", serviceDto.newValue());
        assertEquals("type", serviceDto.newType());
    }

    @Test
    void testModificationServiceDtoToControllerDto() {
        UUID id = UUID.randomUUID();
        ModificationServiceDto serviceDto = new ModificationServiceDto(
                id,
                "old",
                "new",
                "value",
                "type"
        );

        ModificationControllerDto controllerDto = mapper.modificationServiceDtoToControllerDto(serviceDto);

        assertEquals(id, controllerDto.id());
        assertEquals("old", controllerDto.oldName());
        assertEquals("new", controllerDto.newName());
        assertEquals("value", controllerDto.newValue());
        assertEquals("type", controllerDto.newType());
    }

    @Test
    void testModificationToCreateServiceDtoToEntity() {
        ModificationToCreateServiceDto serviceDto = new ModificationToCreateServiceDto(
                "old",
                "new",
                "value",
                "type"
        );

        Pattern mockPattern = Mockito.mock(Pattern.class);

        Modification entity = mapper.modificationToCreateServiceDtoToEntity(serviceDto, mockPattern);

        assertNull(entity.getId());
        assertEquals("old", entity.getOldName());
        assertEquals("new", entity.getNewName());
        assertEquals("value", entity.getNewValue());
        assertEquals("type", entity.getNewType());
        assertEquals(mockPattern, entity.getPattern());
    }

    @Test
    void testModificationToUpdateServiceDtoToEntity() {
        UUID id = UUID.randomUUID();
        ModificationToUpdateServiceDto serviceDto = new ModificationToUpdateServiceDto(
                id,
                "old",
                "new",
                "value",
                "type"
        );

        Pattern mockPattern = Mockito.mock(Pattern.class);

        Modification entity = mapper.modificationToUpdateServiceDtoToEntity(serviceDto, mockPattern);

        assertEquals(id, entity.getId());
        assertEquals("old", entity.getOldName());
        assertEquals("new", entity.getNewName());
        assertEquals("value", entity.getNewValue());
        assertEquals("type", entity.getNewType());
        assertEquals(mockPattern, entity.getPattern());
    }

    @Test
    void testModificationToServiceDto() {
        UUID id = UUID.randomUUID();
        Pattern mockPattern = Mockito.mock(Pattern.class);

        Modification entity = new Modification(
                id,
                "old",
                "new",
                "value",
                "type",
                mockPattern
        );

        ModificationServiceDto serviceDto = mapper.modificationToServiceDto(entity);

        assertEquals(id, serviceDto.id());
        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("value", serviceDto.newValue());
        assertEquals("type", serviceDto.newType());
    }
}