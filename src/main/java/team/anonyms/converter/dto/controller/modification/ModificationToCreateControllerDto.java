package team.anonyms.converter.dto.controller.modification;

public record ModificationToCreateControllerDto(
        String oldName,
        String newName,
        String newValue,
        String newType
) {
}
