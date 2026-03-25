package team.anonyms.converter.services.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public final class ConversionApiService {

    /**
     * <p>
     *     Converts JSON data to XML data.
     * </p>
     *
     * @param body body of an arbitrary JSON request.
     *
     * @return converted XML data from {@code body} in string form.
     *
     * @throws IllegalArgumentException if an unsupported JSON structure for conversion from JSON TO XML was provided.
     */
    public String convertJsonToXml(Map<String, Object> body) {
        try {
            return new XmlMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * <p>
     *     Converts XML data to JSON data.
     * </p>
     *
     * @param body body of an arbitrary XML request.
     *
     * @return converted JSON data from {@code body} in string form.
     *
     * @throws IllegalArgumentException if an unsupported XML structure for conversion from XML TO JSON was provided.
     */
    public String convertXmlToJson(Map<String, Object> body) {
        try {
            return new JsonMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
