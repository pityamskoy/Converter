package team.anonyms.converter.dto.service.modification;

public record ModificationToCreateServiceDto(
        String oldName,
        String newName,
        String newType,
        String newValue
) {
}
