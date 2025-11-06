package com.example.restaurant.config;

import com.example.restaurant.dto.ErrorResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.ConflictException;
import com.example.restaurant.exception.NotFoundException;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleEnum(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            buildError(HttpStatus.BAD_REQUEST, "Giá trị enum không hợp lệ.", request.getRequestURI())
        );
    }
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            buildError(HttpStatus.CONFLICT, "Bản ghi đã được cập nhật bởi người khác, vui lòng thử lại.", request.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Dữ liệu không hợp lệ");
        body.put("errors", fieldErrors);
        body.put("path", request.getRequestURI());
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(body);
    }


    // Helper method để tránh lặp code
    private ErrorResponse buildError(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
