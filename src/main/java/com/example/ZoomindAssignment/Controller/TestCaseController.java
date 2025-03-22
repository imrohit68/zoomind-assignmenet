package com.example.ZoomindAssignment.Controller;

import com.example.ZoomindAssignment.DataTranferObjects.ErrorResponse;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseRequest;
import com.example.ZoomindAssignment.DataTranferObjects.TestCaseResponse;
import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Exceptions.CustomException.NotFoundException;
import com.example.ZoomindAssignment.Service.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    private static final String TEST_CASE_DEFAULT_PATH = "/test-cases";
    private static final String TEST_CASE_ID_PATH = "/test-cases/{id}";

    @PostMapping(
            value = TEST_CASE_DEFAULT_PATH,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Create a new test case",
            description = "This endpoint creates a new test case and returns the created test case details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Test case created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestCaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<TestCaseResponse> createTestCase(@RequestBody @Valid TestCaseRequest testCaseRequest) {
        TestCaseResponse response = testCaseService.createTestCase(testCaseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(
            value = TEST_CASE_ID_PATH,
            produces = MediaType.APPLICATION_JSON_VALUE
    )


    @Operation(
            summary = "Get test case by id",
            description = "This endpoint returns the test case details for the given test case id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case details returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestCaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<TestCaseResponse> getTestCaseById(@PathVariable String id) throws NotFoundException {
        TestCaseResponse response = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            value = TEST_CASE_ID_PATH
    )
    @Operation(
            summary = "Delete test case by id",
            description = "This endpoint deletes the test case for the given test case id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Test case deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Void> deleteTestCaseById(@PathVariable String id) throws NotFoundException {
        testCaseService.deleteTestCaseById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(
            value = TEST_CASE_ID_PATH,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Update test case by id",
            description = "This endpoint updates the test case for the given test case id and returns the updated test case details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestCaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Test case not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<TestCaseResponse> updateTestCaseById(@PathVariable String id, @RequestBody @Valid TestCaseRequest testCaseRequest) throws NotFoundException {
        TestCaseResponse response = testCaseService.updateTestCaseById(id, testCaseRequest);
        return ResponseEntity.ok(response);
    }
    @GetMapping(
            value = TEST_CASE_DEFAULT_PATH,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Get all test cases",
            description = "This endpoint returns all the test cases based on the given status and priority."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test cases returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
    })
    public ResponseEntity<Page<TestCaseResponse>> getAllTestCases(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TestCaseResponse> response = testCaseService.getAllTestCases(status, priority, page, size);
        return ResponseEntity.ok(response);
    }
}
