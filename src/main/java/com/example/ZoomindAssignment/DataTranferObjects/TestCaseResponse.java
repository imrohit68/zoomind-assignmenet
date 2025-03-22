package com.example.ZoomindAssignment.DataTranferObjects;

import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.Instant;
@Schema(description = "Response payload for a test case")
public record TestCaseResponse(
        @Schema(description = "Unique identifier of the test case", example = "60f1b3b3b3b3b3b3b3b3b3b3")
        String id,
        @Schema(description = "Title of the test case", example = "Login Test")
        String title,
        @Schema(description = "Detailed description of the test case", example = "Verify login with valid credentials")
        String description,
        @Schema(description = "Current status of the test case", example = "PENDING")
        Status status,
        @Schema(description = "Priority of the test case", example = "HIGH")
        Priority priority,
        @Schema(description = "Date and time when the test case was created", example = "2021-07-17T12:23:45.000Z")
        Instant createdAt,
        @Schema(description = "Date and time when the test case was last updated", example = "2021-07-17T12:23:45.000Z")
        Instant updatedAt
) implements Serializable {}
