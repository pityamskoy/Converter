package team.anonyms.converter.controllers.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.controllers.frontend.pagination.PaginationHandler;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToUpdateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.services.frontend.PatternService;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"https://cson.site"})
@RequestMapping("/patterns")
public final class PatternController {
    private static final Logger log = LoggerFactory.getLogger(PatternController.class);

    private final PatternService patternService;
    private final PatternMapper patternMapper;
    private final PaginationHandler<PatternControllerDto> paginationHandler;

    public PatternController(
            PatternService patternService,
            PatternMapper patternMapper,
            PaginationHandler<PatternControllerDto> paginationHandler
    ) {
        this.patternService = patternService;
        this.patternMapper = patternMapper;
        this.paginationHandler = paginationHandler;
    }

    @GetMapping("/{userId}/{limit}/{offset}")
    public ResponseEntity<List<PatternControllerDto>> getAllPatternsByUserId(
            @PathVariable UUID userId,
            @PathVariable int limit,
            @PathVariable int offset
    ) {
        log.info("Called getAllPatternsByUserId; id={}", userId);

        List<PatternControllerDto> allPatterns = patternService.getAllPatternsByUserId(userId).
                stream().map(patternMapper::patternServiceDtoToControllerDto).toList();

        return ResponseEntity.ok(paginationHandler.makeSliceFromList(allPatterns, offset, limit));
    }

    @PostMapping
    public ResponseEntity<PatternControllerDto> createPattern(
            @RequestBody PatternToCreateControllerDto patternToCreate
    ) {
        log.info("Called createPattern; patternToCreate={}", patternToCreate);

        PatternToCreateServiceDto patternToCreateServiceDto = patternMapper.
                patternToCreateControllerDtoToService(patternToCreate);

        PatternServiceDto patternCreated = patternService.createPattern(patternToCreateServiceDto);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(patternMapper.patternServiceDtoToControllerDto(patternCreated));
    }

    @PutMapping
    public ResponseEntity<PatternControllerDto> updatePattern(
            @RequestBody PatternToUpdateControllerDto patternToUpdate
    ) {
        log.info("Called updatePattern; patternToUpdateControllerDto={}", patternToUpdate);

        PatternServiceDto patternUpdated = patternService.updatePattern(
                patternMapper.patternToUpdateControllerDtoToService(patternToUpdate)
        );

        return ResponseEntity.ok(patternMapper.patternServiceDtoToControllerDto(patternUpdated));
    }

    @DeleteMapping("/{patternId}")
    public ResponseEntity<Void> deletePattern(@PathVariable UUID patternId) {
        log.info("Called deletePattern; id={}", patternId);

        patternService.deletePattern(patternId);

        return ResponseEntity.noContent().build();
    }
}
