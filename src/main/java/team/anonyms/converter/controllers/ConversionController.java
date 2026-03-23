package team.anonyms.converter.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import team.anonyms.converter.services.ConversionService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"}, exposedHeaders = "*")
@RequestMapping("/conversion")
public final class ConversionController {
    private static final Logger log = LoggerFactory.getLogger(ConversionController.class);

    @Autowired
    private ConversionService conversionService;

    //add separator to return
    @PostMapping(value = "/json_csv", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToCsv(
            @RequestPart(name = "file") MultipartFile file
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToCsv; filename={}", filename);

        try {
            Path csvPath = conversionService.convertJsonFileToCsv(file);
            Objects.requireNonNull(filename);
            log.debug("Converted file created; csvPath={}", csvPath);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(csvPath)) {
                    in.transferTo(outputStream);
                } finally {
                    log.debug("Deleting converted file; csvPath={}", csvPath);
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
    @PostMapping(value = "/csv_json", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertCsvFileToJson(
            @RequestPart(name = "file") MultipartFile file
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertCsvFileToJson; filename={}", filename);

        try {
            Path jsonPath = conversionService.convertCsvFileToJson(file);
            Objects.requireNonNull(filename);
            log.debug("Converted file created; jsonPath={}", jsonPath);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(jsonPath)) {
                    in.transferTo(outputStream);
                } finally {
                    log.debug("Deleting converted file; jsonPath={}", jsonPath);
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

    @PostMapping(value = "/json_xml", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToXml(
            @RequestPart(name = "file") MultipartFile file
    ) {
        String filename = file.getOriginalFilename();
        log.info("Called convertJsonFileToXml; filename={}", filename);

        try {
            Path xmlPath = conversionService.convertJsonFileToXml(file);
            Objects.requireNonNull(filename);
            log.debug("Converted file created; xmlPath={}", xmlPath);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(xmlPath)) {
                    in.transferTo(outputStream);
                } finally {
                    log.debug("Deleting converted file; xmlPath={}", xmlPath);
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
}
