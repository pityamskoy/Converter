package team.anonyms.converter.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team.anonyms.converter.utility.exceptions.UnsupportedExtensionException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // фейк объект контроллера
    @RestController
    private static class DummyController {

        @GetMapping("/test/entity-not-found")
        public void throwEntityNotFound() {
            throw new EntityNotFoundException("Entity not found test");
        }

        @GetMapping("/test/unsupported-extension")
        public void throwUnsupportedExtension() {
            throw new UnsupportedExtensionException("Unsupported extension test");
        }

        @GetMapping("/test/npe")
        public void throwNpe() {
            throw new NullPointerException("NPE test");
        }

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Illegal argument test");
        }
    }

    // настройка
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // тут должно быть 404, 400, 500, 400
    @Test
    void handleEntityNotFoundException_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/entity-not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleUnsupportedExtensionException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/unsupported-extension"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/npe"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/illegal-argument"))
                .andExpect(status().isBadRequest());
    }
}