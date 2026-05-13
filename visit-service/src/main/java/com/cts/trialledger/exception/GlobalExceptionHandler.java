package com.cts.visit.exception;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.dto.ErrorResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 1. Handle Resource Not Found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        return new ResponseEntity<>(
                new ApiResponseDto<>("FAILURE", ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    // 2. Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(
                new ApiResponseDto<>("FAILURE", "Validation failed", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    // 3. Handle illegal state (e.g. business rule violations)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalStateException(
            IllegalStateException ex) {

        return new ResponseEntity<>(
                new ApiResponseDto<>("FAILURE", ex.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }

    // 4. Handle Feign client failures (downstream service errors)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleFeignException(FeignException ex) {

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }

        String message;
        if (ex.status() == 404) {
            message = "Requested resource was not found in the downstream service";
        } else if (ex.status() == -1) {
            message = "Downstream service is unavailable";
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } else {
            message = "Error communicating with downstream service: " + ex.getMessage();
        }

        return new ResponseEntity<>(
                new ApiResponseDto<>("FAILURE", message, null),
                status
        );
    }

    // 5. Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGenericException(Exception ex) {

        return new ResponseEntity<>(
                new ApiResponseDto<>("ERROR", "Internal Server Error", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
