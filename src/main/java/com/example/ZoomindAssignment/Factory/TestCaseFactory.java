package com.example.ZoomindAssignment.Factory;

import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseResponse;
import com.example.ZoomindAssignment.Models.TestCaseModel;

public class TestCaseFactory {
    public static TestCaseModel createTestCase(TestCaseRequest request) {
        return new TestCaseModel(
                request.title(),
                request.description(),
                request.status(),
                request.priority()
        );
    }
    public static TestCaseModel createTestCase(String id, TestCaseRequest request) {
        return new TestCaseModel(
                id,
                request.title(),
                request.description(),
                request.status(),
                request.priority()
        );
    }

    public static TestCaseResponse toResponse(TestCaseModel testCase) {
        return new TestCaseResponse(
                testCase.getId(),
                testCase.getTitle(),
                testCase.getDescription(),
                testCase.getStatus(),
                testCase.getPriority(),
                testCase.getCreatedAt(),
                testCase.getUpdatedAt()
        );
    }
}
