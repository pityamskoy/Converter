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
    private final static Logger log = LoggerFactory.getLogger(ConversionController.class);

    @Autowired
    private ConversionService conversionService;

    @PostMapping(value = "/json_csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> convertJsonFileToCsv(@RequestPart MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = file.getContentType();

        log.info("Called convertJsonFileToCsv; filename={}", fileName);

        if (fileName == null) {
            log.error("fileName is null");
            return ResponseEntity.status(HttpStatus.valueOf(500)).build();
        }

        if (fileExtension == null) {
            log.error("fileExtension is null");
            return ResponseEntity.status(HttpStatus.valueOf(500)).build();
        }

        if (!fileExtension.equals(".json")) {
            log.error("File doesn't have '.json' extension");
            return ResponseEntity.badRequest().build();
        }

        if (file.isEmpty()) {
            log.error("File is empty");
            return ResponseEntity.badRequest().build();
        }

        try {
            Path csvPath = conversionService.convertJsonFileToCsv(file);

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
            return ResponseEntity.status(500).build();
        }
    }
}
