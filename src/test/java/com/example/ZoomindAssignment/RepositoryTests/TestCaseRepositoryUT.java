package com.example.ZoomindAssignment.RepositoryTests;


import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import com.example.ZoomindAssignment.Repository.TestCaseRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.data.domain.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@Testcontainers
public class TestCaseRepositoryUT {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0").withReuse(false);
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestCaseRepository testCaseRepository;

    private TestCaseModel testCase1, testCase2, testCase3;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testCaseRepository.deleteAll();

        testCase1 = new TestCaseModel(null, "Login Test", "Check login functionality", Status.PASSED, Priority.HIGH, Instant.now(), Instant.now());
        testCase2 = new TestCaseModel(null, "Signup Test", "Check signup functionality", Status.FAILED, Priority.MEDIUM, Instant.now(), Instant.now());
        testCase3 = new TestCaseModel(null, "Profile Update Test", "Check profile update", Status.PASSED, Priority.LOW, Instant.now(), Instant.now());

        testCaseRepository.saveAll(List.of(testCase1, testCase2, testCase3));

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Test
    void findByStatus_ShouldReturnEmpty_WhenNoMatches() {
        Page<TestCaseModel> result = testCaseRepository.findByStatus(Status.IN_PROGRESS, pageable);
        assertEquals(0, result.getTotalElements());
    }
    @Test
    void findAll_ShouldRespectPageSize() {
        Pageable smallPageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TestCaseModel> result = testCaseRepository.findAll(smallPageable);
        assertEquals(2, result.getContent().size());
    }


    @Test
    void findByStatusAndPriority_ShouldReturnMatchingTestCases() {
        Page<TestCaseModel> result = testCaseRepository.findByStatusAndPriority(Status.PASSED, Priority.HIGH, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Login Test", result.getContent().get(0).getTitle());
    }

    // ✅ Test findByStatus
    @Test
    void findByStatus_ShouldReturnTestCasesWithGivenStatus() {
        Page<TestCaseModel> result = testCaseRepository.findByStatus(Status.PASSED, pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void findByPriority_ShouldReturnTestCasesWithGivenPriority() {
        Page<TestCaseModel> result = testCaseRepository.findByPriority(Priority.MEDIUM, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Signup Test", result.getContent().get(0).getTitle());
    }

    @Test
    void findAll_ShouldReturnAllTestCasesPaginated() {
        Page<TestCaseModel> result = testCaseRepository.findAll(pageable);

        assertEquals(3, result.getTotalElements());
    }
}
