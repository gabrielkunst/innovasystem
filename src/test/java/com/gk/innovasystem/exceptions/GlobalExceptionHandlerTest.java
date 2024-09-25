package com.gk.innovasystem.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void shouldHandleResourceAlreadyExistsException() {
        ResourceAlreadyExistsException exception = new ResourceAlreadyExistsException("Resource already exists");

        ResponseEntity<String> response = globalExceptionHandler.handleResourceAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Resource already exists", response.getBody());
    }

    @Test
    public void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<String> response = globalExceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    public void shouldHandleInvalidRequestException() {
        InvalidRequestException exception = new InvalidRequestException("Invalid request");

        ResponseEntity<String> response = globalExceptionHandler.handleInvalidRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    public void shouldHandleGenericException() {
        Exception exception = new Exception("An unexpected error occurred");

        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: An unexpected error occurred", response.getBody());
    }
}
