package com.example.ZoomindAssignment.Exceptions;

import com.example.ZoomindAssignment.DataTranferObjects.ErrorResponse;
import com.example.ZoomindAssignment.Exceptions.CustomException.NotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalException extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();
        List<String> errors = validationErrorList.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String errorMessage = "[" + String.join(", ", errors) + "]";
        return new ResponseEntity<>(new ErrorResponse(ex.getClass().getSimpleName(),errorMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException invalidEx) {
            if (invalidEx.getTargetType().isEnum()) {
                String invalidValue = invalidEx.getValue().toString();
                String expectedValues = Arrays.toString(invalidEx.getTargetType().getEnumConstants());
                String message = "Invalid value: '" + invalidValue + "'. Expected: " + expectedValues;
                return new ResponseEntity<>(new ErrorResponse("InvalidEnumValue", message), HttpStatus.BAD_REQUEST);
            }
        }
        // Default message for other cases
        return new ResponseEntity<>(new ErrorResponse("InvalidRequest", mostSpecificCause.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.NOT_FOUND);
    }
}
