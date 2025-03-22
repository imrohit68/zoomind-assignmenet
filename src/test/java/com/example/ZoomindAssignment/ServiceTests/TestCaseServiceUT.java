package com.example.ZoomindAssignment.ServiceTests;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseResponse;
import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Exceptions.CustomException.NotFoundException;
import com.example.ZoomindAssignment.Factory.TestCaseFactory;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import com.example.ZoomindAssignment.Repository.TestCaseRepository;
import com.example.ZoomindAssignment.Service.Implementation.TestCaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestCaseServiceUT {

    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private TestCaseServiceImpl testCaseService;

    private TestCaseRequest testCaseRequest;
    private TestCaseModel testCaseModel;
    private TestCaseResponse expectedResponse;
    private TestCaseModel existingTestCase;
    private TestCaseModel updatedTestCase;
    private static final String TEST_CASE_ID = "123";
    private Pageable pageable;
    private Page<TestCaseModel> testCasePage;

    @BeforeEach
    void setUp() {
        testCaseRequest = new TestCaseRequest("Test Case 1", "Description 1", Status.PENDING, Priority.HIGH);
        testCaseModel = TestCaseFactory.createTestCase(testCaseRequest);
        expectedResponse = TestCaseFactory.toResponse(testCaseModel);

        existingTestCase = new TestCaseModel(TEST_CASE_ID, "Old Title", "Old Description", Status.PENDING, Priority.HIGH, Instant.now(), Instant.now());
        updatedTestCase = TestCaseFactory.createTestCase(TEST_CASE_ID, new TestCaseRequest("Updated Title", "Updated Description", Status.PASSED, Priority.MEDIUM));

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        testCasePage = new PageImpl<>(List.of(existingTestCase, updatedTestCase), pageable, 2);
    }

    @Test
    void createTestCase_ShouldSaveAndReturnResponse() {
        when(testCaseRepository.save(any(TestCaseModel.class))).thenReturn(testCaseModel);
        TestCaseResponse actualResponse = testCaseService.createTestCase(testCaseRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.title(), actualResponse.title());
        assertEquals(expectedResponse.description(), actualResponse.description());

        verify(testCaseRepository, times(1)).save(any(TestCaseModel.class));
    }

    @Test
    void shouldReturnTestCaseWhenIdIsValid() throws NotFoundException {
        when(testCaseRepository.findById(TEST_CASE_ID)).thenReturn(Optional.of(existingTestCase));

        TestCaseResponse response = testCaseService.getTestCaseById(TEST_CASE_ID);

        assertNotNull(response);
        assertEquals(existingTestCase.getTitle(), response.title());
        assertEquals(existingTestCase.getStatus(), response.status());

        verify(testCaseRepository, times(1)).findById(TEST_CASE_ID);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenIdIsInvalid() {
        String invalidTestCaseId = "999";
        when(testCaseRepository.findById(invalidTestCaseId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> testCaseService.getTestCaseById(invalidTestCaseId)
        );

        assertEquals("Test case not found with id: 999", thrown.getMessage());
        verify(testCaseRepository, times(1)).findById(invalidTestCaseId);
    }

    @Test
    void deleteTestCaseById_ShouldDelete_WhenTestCaseExists() {
        when(testCaseRepository.existsById(TEST_CASE_ID)).thenReturn(true);

        assertDoesNotThrow(() -> testCaseService.deleteTestCaseById(TEST_CASE_ID));

        verify(testCaseRepository, times(1)).deleteById(TEST_CASE_ID);
    }

    @Test
    void deleteTestCaseById_ShouldThrowNotFoundException_WhenTestCaseDoesNotExist() {
        String invalidTestCaseId = "999";
        when(testCaseRepository.existsById(invalidTestCaseId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> testCaseService.deleteTestCaseById(invalidTestCaseId));

        assertEquals("Test case not found with id: 999", exception.getMessage());

        verify(testCaseRepository, times(1)).existsById(invalidTestCaseId);
        verify(testCaseRepository, never()).deleteById(anyString());
    }

    @Test
    void  updateTestCaseById_ShouldUpdateAndReturnResponse_WhenTestCaseExists() throws NotFoundException {
        when(testCaseRepository.existsById(TEST_CASE_ID)).thenReturn(true);
        when(testCaseRepository.save(any(TestCaseModel.class))).thenReturn(updatedTestCase);

        TestCaseResponse actualResponse = testCaseService.updateTestCaseById(TEST_CASE_ID, new TestCaseRequest("Updated Title", "Updated Description", Status.PASSED, Priority.MEDIUM));

        assertNotNull(actualResponse);
        assertEquals(updatedTestCase.getTitle(), actualResponse.title());
        assertEquals(updatedTestCase.getDescription(), actualResponse.description());
        assertEquals(updatedTestCase.getStatus(), actualResponse.status());
        assertEquals(updatedTestCase.getPriority(), actualResponse.priority());

        verify(testCaseRepository, times(1)).existsById(TEST_CASE_ID);
        verify(testCaseRepository, times(1)).save(any(TestCaseModel.class));
    }

    @Test
    void getAllTestCases_ShouldReturnPaginatedList_WhenNoFiltersApplied() {
        when(testCaseRepository.findAll(pageable)).thenReturn(testCasePage);

        Page<TestCaseResponse> response = testCaseService.getAllTestCases(null, null, 0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());

        verify(testCaseRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllTestCases_ShouldReturnFilteredResults_WhenFilteringByStatusAndPriority() {
        when(testCaseRepository.findByStatusAndPriority(Status.PASSED, Priority.HIGH, pageable)).thenReturn(testCasePage);

        Page<TestCaseResponse> response = testCaseService.getAllTestCases(Status.PASSED, Priority.HIGH, 0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());

        verify(testCaseRepository, times(1)).findByStatusAndPriority(Status.PASSED, Priority.HIGH, pageable);
    }

    @Test
    void getAllTestCases_ShouldReturnFilteredResults_WhenFilteringByStatusOnly() {
        when(testCaseRepository.findByStatus(Status.PASSED, pageable)).thenReturn(testCasePage);

        Page<TestCaseResponse> response = testCaseService.getAllTestCases(Status.PASSED, null, 0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());

        verify(testCaseRepository, times(1)).findByStatus(Status.PASSED, pageable);
    }

    @Test
    void getAllTestCases_ShouldReturnFilteredResults_WhenFilteringByPriorityOnly() {
        when(testCaseRepository.findByPriority(Priority.HIGH, pageable)).thenReturn(testCasePage);

        Page<TestCaseResponse> response = testCaseService.getAllTestCases(null, Priority.HIGH, 0, 10);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());

        verify(testCaseRepository, times(1)).findByPriority(Priority.HIGH, pageable);
    }
}
