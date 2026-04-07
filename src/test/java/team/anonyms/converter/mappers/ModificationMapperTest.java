package team.anonyms.converter.mappers;

import org.junit.jupiter.api.Test;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.entities.Modification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModificationMapperTest {

    private final ModificationMapper mapper = new ModificationMapper();

    // по сути, тут везде прогон объектов туда-сюда через маппер и проверка, что с ними все норм
    // везде все одинаково во всех 6 методах

    @Test
    void testModificationControllerDtoToServiceDto() {
        UUID id = UUID.randomUUID();
        ModificationControllerDto controllerDto = new ModificationControllerDto(
                id,
                "old",
                "new",
                "type",
                "value"
        );

        ModificationServiceDto serviceDto = mapper.modificationControllerDtoToServiceDto(controllerDto);

        assertEquals(id, serviceDto.id());
        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("type", serviceDto.newType());
        assertEquals("value", serviceDto.newValue());
    }

    @Test
    void testModificationToCreateControllerDtoToService() {
        ModificationToCreateControllerDto controllerDto = new ModificationToCreateControllerDto(
                "old",
                "new",
                "type",
                "value"
        );

        ModificationToCreateServiceDto serviceDto = mapper.modificationToCreateControllerDtoToService(controllerDto);

        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("type", serviceDto.newType());
        assertEquals("value", serviceDto.newValue());
    }

    @Test
    void testModificationServiceDtoToControllerDto() {
        UUID id = UUID.randomUUID();
        ModificationServiceDto serviceDto = new ModificationServiceDto(
                id,
                "old",
                "new",
                "type",
                "value"
        );

        ModificationControllerDto controllerDto = mapper.modificationServiceDtoToControllerDto(serviceDto);

        assertEquals(id, controllerDto.id());
        assertEquals("old", controllerDto.oldName());
        assertEquals("new", controllerDto.newName());
        assertEquals("type", controllerDto.newType());
        assertEquals("value", controllerDto.newValue());
    }

    @Test
    void testModificationServiceDtoToEntity() {
        UUID id = UUID.randomUUID();
        ModificationServiceDto serviceDto = new ModificationServiceDto(
                id,
                "old",
                "new",
                "type",
                "value"
        );

        Modification entity = mapper.modificationServiceDtoToEntity(serviceDto);

        assertEquals(id, entity.getId());
        assertEquals("old", entity.getOldName());
        assertEquals("new", entity.getNewName());
        assertEquals("type", entity.getNewType());
        assertEquals("value", entity.getNewValue());
    }

    @Test
    void testModificationToCreateServiceDtoToEntity() {
        ModificationToCreateServiceDto serviceDto = new ModificationToCreateServiceDto(
                "old",
                "new",
                "type",
                "value"
        );

        Modification entity = mapper.modificationToCreateServiceDtoToEntity(serviceDto);

        assertNotNull(entity.getId());

        assertEquals("old", entity.getOldName());
        assertEquals("new", entity.getNewName());
        assertEquals("type", entity.getNewType());
        assertEquals("value", entity.getNewValue());
    }

    @Test
    void testModificationToServiceDto() {
        UUID id = UUID.randomUUID();
        Modification entity = new Modification(id,
                "old",
                "new",
                "type",
                "value"
        );

        ModificationServiceDto serviceDto = mapper.modificationToServiceDto(entity);

        assertEquals(id, serviceDto.id());
        assertEquals("old", serviceDto.oldName());
        assertEquals("new", serviceDto.newName());
        assertEquals("type", serviceDto.newType());
        assertEquals("value", serviceDto.newValue());
    }
}