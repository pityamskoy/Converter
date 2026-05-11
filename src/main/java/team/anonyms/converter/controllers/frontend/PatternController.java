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
@RequestMapping("/patterns")
public class PatternController {
    private static final Logger logger = LoggerFactory.getLogger(PatternController.class);

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
    public ResponseEntity<List<PatternControllerDto>> getPatternsByUserId(
            @PathVariable UUID userId,
            @PathVariable int limit,
            @PathVariable int offset
    ) {
        logger.info("Called getPatternsByUserId; id={}", userId);

        List<PatternControllerDto> allPatterns = patternService.getAllPatternsByUserId(userId).stream()
                .map(patternMapper::patternServiceDtoToControllerDto)
                .toList();

        return ResponseEntity.ok(paginationHandler.makeSliceFromList(allPatterns, offset, limit));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Long> getNumberOfAllPatternsByUserId(@PathVariable UUID userId) {
        logger.info("Called getNumberOfPatternsByUserId; userId={}", userId);

        return ResponseEntity.ok(patternService.getNumberOfAllPatternsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<PatternControllerDto> createPattern(
            @RequestBody PatternToCreateControllerDto patternToCreate
    ) {
        logger.info("Called createPattern; patternToCreate={}", patternToCreate);

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
        logger.info("Called updatePattern; patternToUpdateControllerDto={}", patternToUpdate);

        PatternServiceDto patternUpdated = patternService.updatePattern(
                patternMapper.patternToUpdateControllerDtoToService(patternToUpdate)
        );

        return ResponseEntity.ok(patternMapper.patternServiceDtoToControllerDto(patternUpdated));
    }

    @DeleteMapping("/{patternId}")
    public ResponseEntity<Void> deletePattern(@PathVariable UUID patternId) {
        logger.info("Called deletePattern; id={}", patternId);

        patternService.deletePattern(patternId);

        return ResponseEntity.noContent().build();
    }
}
