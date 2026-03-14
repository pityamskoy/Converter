package team.anonyms.converter.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.dto.controller.modification.ModificationToCreateControllerDto;
import team.anonyms.converter.dto.service.modification.ModificationServiceDto;
import team.anonyms.converter.dto.service.modification.ModificationToCreateServiceDto;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.services.ModificationService;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/modifications")
public final class ModificationController {
    private final static Logger log = LoggerFactory.getLogger(ModificationController.class);

    @Autowired
    private ModificationService modificationService;
    @Autowired
    private ModificationMapper modificationMapper;

    @GetMapping
    public ResponseEntity<List<ModificationControllerDto>> getAllModificationsByPatternId(@RequestBody UUID patternId) {
        log.info("Called getAllModificationsByPatternId; id={}", patternId);

        return ResponseEntity.ok(modificationService.getAllModificationsByPatternId(patternId).stream().
                map(modificationMapper::modificationServiceDtoToControllerDto).toList());
    }

    @PostMapping("/create")
    public ResponseEntity<ModificationControllerDto> createModification(
            @RequestBody ModificationToCreateControllerDto modificationToCreate
    ) {
        log.info("Called createModification; modificationToCreate={}", modificationToCreate);

        ModificationToCreateServiceDto modificationToCreateServiceDto = modificationMapper.
                modificationToCreateControllerDtoToService(modificationToCreate);

        ModificationServiceDto modificationCreated = modificationService.
                createModification(modificationToCreateServiceDto);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(modificationMapper.modificationServiceDtoToControllerDto(modificationCreated));
    }

    @PutMapping("/update")
    public ResponseEntity<ModificationControllerDto> updateModification(
            @RequestBody ModificationControllerDto modificationToUpdate
    ) {
        log.info("Called updateModification; modificationToUpdate={}", modificationToUpdate);

        ModificationServiceDto modificationUpdated = modificationService.updateModification(
                modificationMapper.modificationControllerDtoToServiceDto(modificationToUpdate));

        return ResponseEntity.ok(modificationMapper.modificationServiceDtoToControllerDto(modificationUpdated));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteModification(@RequestBody UUID modificationId) {
        log.info("Called deleteModification; id={}", modificationId);

        modificationService.deleteModification(modificationId);

        return ResponseEntity.noContent().build();
    }
}
