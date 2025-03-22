package com.example.ZoomindAssignment.Exceptions.CustomException;

public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

    /**
     * We don't need to print out stack traces for NotFoundException.
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
