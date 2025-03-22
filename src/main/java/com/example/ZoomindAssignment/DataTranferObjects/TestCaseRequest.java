package com.example.ZoomindAssignment.DataTranferObjects;


import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Schema(description = "Request payload for creating a test case")
public record TestCaseRequest(

        @Schema(description = "Title of the test case", example = "Login Test")
        @NotBlank(message = "Title cannot be empty")
        String title,

        @Schema(description = "Detailed description of the test case", example = "Verify login with valid credentials")
        String description,

        @Schema(description = "Current status of the test case", example = "PENDING")
        @NotNull(message = "Status cannot be null")
        Status status,

        @Schema(description = "Priority of the test case", example = "HIGH")
        @NotNull(message = "Priority cannot be null")
        Priority priority

) {}
