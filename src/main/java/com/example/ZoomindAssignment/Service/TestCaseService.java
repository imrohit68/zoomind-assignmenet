package com.example.ZoomindAssignment.Service;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseResponse;
import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Exceptions.CustomException.NotFoundException;
import com.example.ZoomindAssignment.Factory.TestCaseFactory;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import com.example.ZoomindAssignment.Repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


public interface TestCaseService {
    TestCaseResponse createTestCase(TestCaseRequest testCaseRequest);
    TestCaseResponse getTestCaseById(String id) throws NotFoundException;
    void deleteTestCaseById(String id) throws NotFoundException;
    TestCaseResponse updateTestCaseById(String id, TestCaseRequest testCaseRequest) throws NotFoundException;

    Page<TestCaseResponse> getAllTestCases(Status status, Priority priority, int page, int size);

}
