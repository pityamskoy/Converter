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

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/conversion")
public final class ConversionController {
    private static final Logger log = LoggerFactory.getLogger(ConversionController.class);

    @Autowired
    private ConversionService conversionService;

    @PostMapping(value = "/json_csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToCsv(@RequestPart(name = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("Called convertJsonFileToCsv; filename={}", fileName);

        try {
            Path csvPath = conversionService.convertJsonFileToCsv(file);
            log.debug("CSV file created at {}", csvPath);

            StreamingResponseBody stream = outputStream -> {
                try (InputStream in = Files.newInputStream(csvPath)) {
                    in.transferTo(outputStream);
                } finally {
                    log.debug("Deleting temp file={}", csvPath);
                    Files.deleteIfExists(csvPath);
                }
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().
                    filename(csvPath.getFileName().toString()).build());
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentLength(Files.size(csvPath));

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
