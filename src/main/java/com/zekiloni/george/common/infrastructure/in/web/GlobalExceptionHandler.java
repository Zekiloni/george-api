package com.zekiloni.george.common.infrastructure.in.web;

import com.zekiloni.george.commerce.application.usecase.OrderValidationException;
import com.zekiloni.george.commerce.domain.catalog.exception.OfferingPricingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Translates well-known domain/application exceptions to HTTP status codes
 * with a consistent JSON body so the frontend can show toasts without parsing
 * stack traces.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<Map<String, Object>> handleOrderValidation(OrderValidationException ex) {
        return badRequest("ORDER_VALIDATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(OfferingPricingException.class)
    public ResponseEntity<Map<String, Object>> handleOfferingPricing(OfferingPricingException ex) {
        log.warn("Offering pricing error: {}", ex.getMessage());
        return badRequest("OFFERING_PRICING_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return badRequest("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body("NOT_FOUND", ex.getMessage()));
    }

    private ResponseEntity<Map<String, Object>> badRequest(String code, String message) {
        return ResponseEntity.badRequest().body(body(code, message));
    }

    private Map<String, Object> body(String code, String message) {
        return Map.of(
                "error", code,
                "message", message == null ? "" : message,
                "timestamp", OffsetDateTime.now().toString()
        );
    }
}
