package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(m);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(m);
    }

    // ✅ Handle invalid JSON or wrong date format
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", "Invalid input format. For dates, please use yyyy-MM-dd.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(m);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(m);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        Map<String, String> m = new HashMap<>();
        m.put("error", "internal error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(m);
    }
}
