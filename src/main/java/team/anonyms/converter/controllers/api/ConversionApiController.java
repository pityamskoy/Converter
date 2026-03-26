package team.anonyms.converter.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.anonyms.converter.services.api.ConversionApiService;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/direct/conversion")
public final class ConversionApiController {
    private static final Logger log = LoggerFactory.getLogger(ConversionApiController.class);

    @Autowired
    private ConversionApiService conversionApiService;

    @PostMapping(
            value = "/json/xml",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> convertJsonToXml(@RequestBody Map<String, Object> body) {
        log.info("Called convertJsonToXml, body={}", body);
        return ResponseEntity.ok(conversionApiService.convertJsonToXml(body));
    }

    @PostMapping(
            value = "/xml/json",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> convertXmlToJson(@RequestBody Map<String, Object> body) {
        log.info("Called convertXmlToJson, body={}", body);
        return ResponseEntity.ok(conversionApiService.convertXmlToJson(body));
    }
}
