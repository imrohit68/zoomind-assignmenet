package com.example.ZoomindAssignment.DataTranferObjects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Timestamp;
import java.util.Date;
@Schema(description = "Response payload for an error response")
public record ErrorResponse(
        @Schema(description = "Name of the exception", example = "NullPointerException")
        String exception,
        @Schema(description = "Error message", example = "An error occurred while processing the request")
        String message,
        @Schema(description = "Timestamp when the error occurred", example = "2021-07-17T12:23:45.000Z")
        Timestamp timestamp) {
    public ErrorResponse(String exception, String message) {
        this(exception, message,new Timestamp(new Date().getTime()));
    }

    public ErrorResponse(Throwable cause) {
        this(cause.getClass().getSimpleName(), cause.getMessage());
    }
}
