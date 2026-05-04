package team.anonyms.converter.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.services.api.ConversionApiService;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/direct/conversion")
public class ConversionApiController {
    private static final Logger logger = LoggerFactory.getLogger(ConversionApiController.class);

    private final ConversionApiService conversionApiService;

    public ConversionApiController(ConversionApiService conversionApiService) {
        this.conversionApiService = conversionApiService;
    }

    @PostMapping(
            value = "/json/xml",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> convertJsonToXml(@RequestBody Map<String, Object> body) {
        logger.info("Called convertJsonToXml, body={}", body);
        return ResponseEntity.ok(conversionApiService.convertJsonToXml(body));
    }

    @PostMapping(
            value = "/xml/json",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> convertXmlToJson(@RequestBody Map<String, Object> body) {
        logger.info("Called convertXmlToJson, body={}", body);
        return ResponseEntity.ok(conversionApiService.convertXmlToJson(body));
    }
}
