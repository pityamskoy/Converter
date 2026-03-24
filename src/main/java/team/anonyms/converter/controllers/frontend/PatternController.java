package team.anonyms.converter.controllers.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.dto.controller.pattern.PatternToCreateControllerDto;
import team.anonyms.converter.dto.service.pattern.PatternServiceDto;
import team.anonyms.converter.dto.service.pattern.PatternToCreateServiceDto;
import team.anonyms.converter.mappers.PatternMapper;
import team.anonyms.converter.services.frontend.PatternService;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/patterns")
public final class PatternController {
    private static final Logger log = LoggerFactory.getLogger(PatternController.class);

    @Autowired
    private PatternService patternService;
    @Autowired
    private PatternMapper patternMapper;

    @GetMapping
    public ResponseEntity<List<PatternControllerDto>> getAllPatternsByUserId(@RequestBody UUID userId) {
        log.info("Called getAllPatternsByUserId; id={}", userId);

        return ResponseEntity.ok(patternService.getAllPatternsByUserId(userId).stream().
                map(patternMapper::patternServiceDtoToControllerDto).toList());
    }

    @PostMapping("/create")
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

    @PutMapping("/update")
    public ResponseEntity<PatternControllerDto> updatePattern(@RequestBody PatternControllerDto patternToUpdate) {
        log.info("Called updatePattern; patternToUpdate={}", patternToUpdate);

        PatternServiceDto patternUpdated = patternService.updatePattern(
                patternMapper.patternControllerDtoToServiceDto(patternToUpdate));

        return ResponseEntity.ok(patternMapper.patternServiceDtoToControllerDto(patternUpdated));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePattern(@RequestBody UUID patternId) {
        log.info("Called deletePattern; id={}", patternId);

        patternService.deletePattern(patternId);

        return ResponseEntity.noContent().build();
    }
}
