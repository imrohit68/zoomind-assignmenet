package com.example.ZoomindAssignment.IntegrationTests;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import com.example.ZoomindAssignment.Repository.TestCaseRepository;
import com.example.ZoomindAssignment.Config.TestCacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestCacheConfig.class)
public class TestCaseControllerIT {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TestCaseModel testCase1, testCase2;

    @BeforeEach
    void setUp() {
        testCaseRepository.deleteAll();

        testCase1 = new TestCaseModel(null, "Login Test", "Check login functionality", Status.PASSED, Priority.HIGH, Instant.now(), Instant.now());
        testCase2 = new TestCaseModel(null, "Signup Test", "Check signup functionality", Status.FAILED, Priority.MEDIUM, Instant.now(), Instant.now());

        testCaseRepository.saveAll(List.of(testCase1, testCase2));
    }

    @Test
    void getAllTestCases_ShouldReturnAllTestCases() throws Exception {
        mockMvc.perform(get("/api/v1/test-cases")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", containsInAnyOrder("Signup Test", "Login Test")));  // ✅ Order-independent check

    }

    @Test
    void getTestCaseById_ShouldReturnTestCase() throws Exception {
        mockMvc.perform(get("/api/v1/test-cases/" + testCase1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Login Test")));
    }

    @Test
    void getTestCaseById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/test-cases/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTestCase_ShouldCreateAndReturnTestCase() throws Exception {
        TestCaseRequest newTestCase = new TestCaseRequest("New Test", "New Description", Status.PENDING, Priority.LOW);

        mockMvc.perform(post("/api/v1/test-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestCase)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Test")));
    }

    @Test
    void updateTestCaseById_ShouldUpdateAndReturnTestCase() throws Exception {
        TestCaseRequest updatedRequest = new TestCaseRequest("Updated Test", "Updated Description", Status.IN_PROGRESS, Priority.MEDIUM);

        mockMvc.perform(put("/api/v1/test-cases/" + testCase1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Test")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void updateTestCaseById_ShouldReturnNotFound() throws Exception {
        TestCaseRequest updatedRequest = new TestCaseRequest("Updated Test", "Updated Description", Status.IN_PROGRESS, Priority.MEDIUM);

        mockMvc.perform(put("/api/v1/test-cases/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTestCaseById_ShouldDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/test-cases/" + testCase1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(testCaseRepository.findById(testCase1.getId()).isPresent());
    }

    @Test
    void deleteTestCaseById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/test-cases/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
