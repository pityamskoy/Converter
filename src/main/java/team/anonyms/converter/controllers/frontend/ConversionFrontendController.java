package team.anonyms.converter.controllers.frontend;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import team.anonyms.converter.dto.controller.pattern.PatternControllerDto;
import team.anonyms.converter.services.frontend.ConversionFrontendService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.springframework.http.MediaType.*;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"}, exposedHeaders = "*")
@RequestMapping("/conversion")
public final class ConversionFrontendController {
    private static final Logger log = LoggerFactory.getLogger(ConversionFrontendController.class);

    private final ConversionFrontendService conversionFrontendService;

    public ConversionFrontendController(ConversionFrontendService conversionFrontendService) {
        this.conversionFrontendService = conversionFrontendService;
    }

    /**
     *
     * @param path path to converted file.
     * @param outputFilename output filename.
     * @param mediaType media type of response file.
     *
     * @return prepared {@link ResponseEntity}, which can be transferred right into return statement.
     */
    private @NonNull ResponseEntity<StreamingResponseBody> getResponseEntityForConversionEndpoints(
            @NonNull Path path,
            @NonNull String outputFilename,
            @NonNull MediaType mediaType
    ) throws IOException {
        StreamingResponseBody stream = outputStream -> {
            try (InputStream in = Files.newInputStream(path)) {
                in.transferTo(outputStream);
            } finally {
                Files.deleteIfExists(path);
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(outputFilename).build());
        headers.setContentType(mediaType);
        headers.setContentLength(Files.size(path));

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    //add separator to return
    @PostMapping(value = "/json/csv", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToCsv(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToCsv; filename={}; pattern={}", filename, pattern);

        try {
            Path csvPath = conversionFrontendService.convertJsonFileToCsv(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 5) + ".csv";

            return getResponseEntityForConversionEndpoints(csvPath, outputFilename, parseMediaType("text/csv"));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //fix separator problem
    @PostMapping(value = "/csv/json", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCsvFileToJson(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertCsvFileToJson; filename={}; pattern={}", filename, pattern);

        try {
            Path jsonPath = conversionFrontendService.convertCsvFileToJson(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 4) + ".json";

            return getResponseEntityForConversionEndpoints(jsonPath, outputFilename, APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/json/xml", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToXml(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToXml; filename={}; pattern={}", filename, pattern);

        try {
            Path xmlPath = conversionFrontendService.convertJsonFileToXml(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 5) + ".xml";

            return getResponseEntityForConversionEndpoints(xmlPath, outputFilename, APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/xml/json", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertXmlFileToJson(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertXmlFileToJson; filename={}; pattern={}", filename, pattern);

        try {
            Path jsonPath = conversionFrontendService.convertXmlFileToJson(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 4) + ".json";

            return getResponseEntityForConversionEndpoints(jsonPath, outputFilename, APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //add separator to return
    @PostMapping(value = "/xml/csv", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertXmlFileToCsv(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertXmlFileToCsv; filename={}; pattern={}", filename, pattern);

        try {
            Path csvPath = conversionFrontendService.convertXmlFileToCsv(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 4) + ".csv";

            return getResponseEntityForConversionEndpoints(csvPath, outputFilename, parseMediaType("text/csv"));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //fix separator problem
    @PostMapping(value = "/csv/xml", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCsvFileToXml(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "pattern", required = false) PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertCsvFileToXml; filename={}; pattern={}", filename, pattern);

        try {
            Path xmlPath = conversionFrontendService.convertCsvFileToXml(file, pattern);

            Objects.requireNonNull(filename);
            String outputFilename = filename.substring(0, filename.length() - 4) + ".xml";

            return getResponseEntityForConversionEndpoints(xmlPath, outputFilename, APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
