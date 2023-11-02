package com.ordina_assessment.word_count.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    private ResponseEntity<ErrorResponse> buildResponse(String error, String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(error, message, status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String error = "Method Argument Not Valid";
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String error = "Constraint Violation";
        // Customize message as needed, e.g., extracting property path and message
        String message = ex.getConstraintViolations().stream()
                .map(violation -> String.format("The parameter '%s' %s",
                        violation.getPropertyPath(),
                        violation.getMessage()))
                .collect(Collectors.joining(". "));
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String error = "Missing Request Parameter";
        String message = ex.getParameterName() + " parameter is missing";
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(TypeMismatchException ex) {
        String error = "Type Mismatch";
        String message = String.format("The parameter '%s' should have a value of type '%s'",
                ex.getPropertyName(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = "Method Argument Type Mismatch";
        String message = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = "Illegal Argument";
        String message = ex.getMessage();
        return buildResponse(error, message, HttpStatus.BAD_REQUEST);
    }

    // Generic exception handler as a fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String error = "Internal Server Error";
        String message = "An unexpected error occurred";
        return buildResponse(error, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


