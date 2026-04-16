package team.anonyms.converter.configs;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public static JsonMapper jsonMapper() {
        return new JsonMapper();
    }

    @Bean
    public static XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public static CsvMapper csvMapper() {
        return new CsvMapper();
    }
}
