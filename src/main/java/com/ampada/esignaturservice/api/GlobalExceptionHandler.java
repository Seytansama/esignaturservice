package com.ampada.esignaturservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Globaler Fehlerhandler für alle REST-Endpunkte.
 * Fängt Exceptions ab und gibt einheitliche JSON-Fehlerantworten zurück.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Behandelt nicht gefundene Ressourcen (z.B. unbekannte ID).
     * Gibt HTTP 404 zurück.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "message", ex.getMessage()
        ));
    }

    /**
     * Behandelt Validierungsfehler bei @Valid-annotierten Request Bodies.
     * Gibt HTTP 400 mit dem ersten Validierungsfehler zurück.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // Ersten Fehler aus der Liste holen
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validierungsfehler");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "message", message
        ));
    }
}
