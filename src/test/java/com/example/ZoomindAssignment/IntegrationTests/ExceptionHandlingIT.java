package com.example.ZoomindAssignment.IntegrationTests;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.Config.TestCacheConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestCacheConfig.class)
public class ExceptionHandlingIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNotFound_WhenTestCaseDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/test-cases/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("NotFoundException")))
                .andExpect(jsonPath("$.message", containsString("Test case not found")));
    }

    @Test
    void shouldReturnBadRequest_WhenTestCaseRequestIsInvalid() throws Exception {
        TestCaseRequest invalidRequest = new TestCaseRequest("", "", null, null);

        mockMvc.perform(post("/api/v1/test-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException")))
                .andExpect(jsonPath("$.message", containsString("Title cannot be empty")))
                .andExpect(jsonPath("$.message", containsString("Status cannot be null")))
                .andExpect(jsonPath("$.message", containsString("Priority cannot be null")));
    }

    @Test
    void shouldReturnBadRequest_WhenInvalidEnumValueProvided() throws Exception {
        String invalidJson = """
                {
                  "title": "Invalid Test",
                  "description": "This has an invalid status",
                  "status": "INVALID_STATUS",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/v1/test-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("InvalidEnumValue")))
                .andExpect(jsonPath("$.message", containsString("Invalid value: 'INVALID_STATUS'")));
    }


    @Test
    void shouldReturnBadRequest_WhenJsonIsMalformed() throws Exception {
        String malformedJson = "{ \"title\": \"Missing closing bracket\" ";

        mockMvc.perform(post("/api/v1/test-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("InvalidRequest")))
                .andExpect(jsonPath("$.message", containsString("Unexpected end-of-input")));
    }
}
