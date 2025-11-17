package com.example.barbie_beauty_salon.exceptions;

import com.example.barbie_beauty_salon.dto.ResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String detail = "Validation failed: " + errors;
        return ResponseEntity.badRequest().body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                detail
        ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO> handleValidation(ValidationException e) {
        return ResponseEntity.badRequest().body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                e.getMessage()
        ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseDTO> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.badRequest().body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Token error",
                e.getMessage()
        ));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseDTO> handleSecurity(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                e.getMessage() != null ? e.getMessage() : "You do not have permission to perform this action"
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                "Your role does not permit this operation"
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDTO> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed",
                e.getMessage() != null ? e.getMessage() : "Invalid credentials"
        ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                e.getMessage() != null ? e.getMessage() : "The requested resource does not exist"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGeneric(Exception e) {
        // В продакшене — логировать stack trace, но клиенту не показывать
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                "An unexpected error occurred"
        ));
    }
}
