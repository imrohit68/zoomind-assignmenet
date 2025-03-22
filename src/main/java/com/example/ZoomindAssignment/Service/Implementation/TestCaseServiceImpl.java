package com.example.ZoomindAssignment.Service.Implementation;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseResponse;
import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Exceptions.CustomException.NotFoundException;
import com.example.ZoomindAssignment.Factory.TestCaseFactory;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import com.example.ZoomindAssignment.Repository.TestCaseRepository;
import com.example.ZoomindAssignment.Service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestCaseServiceImpl implements TestCaseService {
    private final TestCaseRepository testCaseRepository;

    @Override
    public TestCaseResponse createTestCase(TestCaseRequest testCaseRequest) {
        log.info("Creating a new test case with title: {}", testCaseRequest.title());
        TestCaseModel save = testCaseRepository.save(TestCaseFactory.createTestCase(testCaseRequest));
        log.info("Test case created successfully with ID: {}", save.getId());
        return TestCaseFactory.toResponse(save);
    }

    @Cacheable(value = "testCases", key = "#id")
    @Override
    public TestCaseResponse getTestCaseById(String id) throws NotFoundException {
        log.info("Fetching test case with ID: {}", id);
        return testCaseRepository.findById(id)
                .map(TestCaseFactory::toResponse)
                .orElseThrow(() -> {
                    log.warn("Test case not found with ID: {}", id);  // ✅ Use WARN instead of ERROR
                    return new NotFoundException("Test case not found with id: " + id);
                });
    }
    @CacheEvict(value = {"testCases", "testCasesList"}, key = "#id", allEntries = true)
    @Override
    public void deleteTestCaseById(String id) throws NotFoundException {
        log.info("Attempting to delete test case with ID: {}", id);
        if (!testCaseRepository.existsById(id)) {
            log.warn("Delete failed - Test case not found with ID: {}", id);  // ✅ WARN instead of ERROR
            throw new NotFoundException("Test case not found with id: " + id);
        }
        testCaseRepository.deleteById(id);
        log.info("Test case deleted successfully with ID: {}", id);
    }

    @CacheEvict(value = {"testCases", "testCasesList"}, key = "#id", allEntries = true)
    @Override
    public TestCaseResponse updateTestCaseById(String id, TestCaseRequest testCaseRequest) throws NotFoundException {
        log.info("Updating test case with ID: {}", id);
        if (!testCaseRepository.existsById(id)) {
            log.warn("Update failed - Test case not found with ID: {}", id);  // ✅ WARN instead of ERROR
            throw new NotFoundException("Test case not found with id: " + id);
        }
        TestCaseModel testCaseModel = TestCaseFactory.createTestCase(id, testCaseRequest);
        TestCaseModel updatedTestCase = testCaseRepository.save(testCaseModel);
        log.info("Test case updated successfully with ID: {}", id);
        return TestCaseFactory.toResponse(updatedTestCase);
    }

    @Cacheable(value = "testCasesList", key = "{#status, #priority, #page, #size}")
    @Override
    public Page<TestCaseResponse> getAllTestCases(Status status, Priority priority, int page, int size) {
        log.debug("Fetching test cases with filters - Status: {}, Priority: {}, Page: {}, Size: {}",
                status, priority, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TestCaseModel> testCases;
        if (status != null && priority != null) {
            testCases = testCaseRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            testCases = testCaseRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            testCases = testCaseRepository.findByPriority(priority, pageable);
        } else {
            testCases = testCaseRepository.findAll(pageable);
        }

        log.debug("Retrieved {} test cases from database", testCases.getTotalElements());
        return testCases.map(TestCaseFactory::toResponse);
    }
}
