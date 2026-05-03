package team.anonyms.converter.controllers.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.controllers.frontend.pagination.PaginationHandler;
import team.anonyms.converter.dto.controller.modification.ModificationControllerDto;
import team.anonyms.converter.mappers.ModificationMapper;
import team.anonyms.converter.services.frontend.ModificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/modifications")
public class ModificationController {
    private static final Logger log = LoggerFactory.getLogger(ModificationController.class);

    private final ModificationService modificationService;
    private final ModificationMapper modificationMapper;
    private final PaginationHandler<ModificationControllerDto> paginationHandler;

    public ModificationController(
            ModificationService modificationService,
            ModificationMapper modificationMapper,
            PaginationHandler<ModificationControllerDto> paginationHandler
    ) {
        this.modificationService = modificationService;
        this.modificationMapper = modificationMapper;
        this.paginationHandler = paginationHandler;
    }

    @GetMapping("/{patternId}/{limit}/{offset}")
    public ResponseEntity<List<ModificationControllerDto>> getModificationsByPatternId(
            @PathVariable UUID patternId,
            @PathVariable int limit,
            @PathVariable int offset
    ) {
        log.info("Called getAllModificationsByPatternId; id={}", patternId);

        List<ModificationControllerDto> allModifications = modificationService.getAllModificationsByPatternId(patternId)
                .stream().map(modificationMapper::modificationServiceDtoToControllerDto)
                .toList();

        return ResponseEntity.ok(paginationHandler.makeSliceFromList(allModifications, offset, limit));
    }

    @GetMapping("/{patternId}")
    public ResponseEntity<Integer> getNumberOfAllModificationsByPatternId(
            @PathVariable UUID patternId
    ) {
        log.info("Called getNumberOfModificationsByPatternId; patternId={}", patternId);

        return ResponseEntity.ok(modificationService.getNumberOfAllModificationsByPatternId(patternId));
    }
}
