package team.anonyms.converter.controllers.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"}, exposedHeaders = "*")
@RequestMapping("/conversion")
public final class ConversionFrontendController {
    private static final Logger log = LoggerFactory.getLogger(ConversionFrontendController.class);

    @Autowired
    private ConversionFrontendService conversionFrontendService;

    //add separator to return
    @PostMapping(value = "/json/csv", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToCsv(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToCsv; filename={}; pattern={}", filename, pattern);

        try {
            Path csvPath = conversionFrontendService.convertJsonFileToCsv(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(csvPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(csvPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 5) + ".csv";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().
                    filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentLength(Files.size(csvPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //fix separator problem
    @PostMapping(value = "/csv/json", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCsvFileToJson(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertCsvFileToJson; filename={}; pattern={}", filename, pattern);

        try {
            Path jsonPath = conversionFrontendService.convertCsvFileToJson(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(jsonPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(jsonPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 4) + ".json";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(jsonPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/json/xml", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToXml(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToXml; filename={}; pattern={}", filename, pattern);

        try {
            Path xmlPath = conversionFrontendService.convertJsonFileToXml(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(xmlPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(xmlPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 5) + ".xml";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().
                    filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(xmlPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/xml/json", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertXmlFileToJson(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertXmlFileToJson; filename={}; pattern={}", filename, pattern);

        try {
            Path jsonPath = conversionFrontendService.convertXmlFileToJson(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(jsonPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(jsonPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 4) + ".json";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(jsonPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //add separator to return
    @PostMapping(value = "/xml/csv", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertXmlFileToCsv(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertXmlFileToCsv; filename={}; pattern={}", filename, pattern);

        try {
            Path csvPath = conversionFrontendService.convertXmlFileToCsv(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(csvPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(csvPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 4) + ".csv";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().
                    filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentLength(Files.size(csvPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //fix separator problem
    @PostMapping(value = "/csv/xml", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCsvFileToXml(
            @RequestPart(name = "file") MultipartFile file,
            @RequestBody PatternControllerDto pattern
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertCsvFileToXml; filename={}; pattern={}", filename, pattern);

        try {
            Path xmlPath = conversionFrontendService.convertCsvFileToXml(file, pattern);
            Objects.requireNonNull(filename);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(xmlPath)) {
                    in.transferTo(outputStream);
                } finally {
                    Files.deleteIfExists(xmlPath);
                }
            };

            String outputFilename = filename.substring(0, filename.length() - 4) + ".xml";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(outputFilename)
                    .build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(xmlPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
