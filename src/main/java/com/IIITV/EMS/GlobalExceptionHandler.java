
package com.IIITV.EMS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> problem(
            String type,
            String title,
            int status,
            String detail) {

        return Map.of(
                "type", type,
                "title", title,
                "status", status,
                "detail", detail
        );
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status,
            String type,
            String detail) {

        return ResponseEntity.status(status)
                .contentType(MediaType.parseMediaType(
                        "application/problem+json;charset=UTF-8"))
                .body(problem(
                        type,
                        status.getReasonPhrase(),
                        status.value(),
                        detail
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        String type = switch (status) {
            case NOT_FOUND -> "user-not-found";
            case CONFLICT -> "illegal-transition";
            case UNPROCESSABLE_ENTITY -> "validation-failed";
            case TOO_MANY_REQUESTS -> "rate-limit-exceeded";
            default -> "request-failed";
        };

        return error(
                status,
                type,
                ex.getReason() != null
                        ? ex.getReason()
                        : status.getReasonPhrase()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(
            HttpMessageNotReadableException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                "malformed-request",
                "Malformed JSON request or invalid field format"
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(
            MissingRequestHeaderException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                "missing-required-header",
                "Required header is missing: " + ex.getHeaderName()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Invalid value"
                ))
                .toList();

        Map<String, Object> body = new java.util.LinkedHashMap<>(
                problem(
                        "validation-failed",
                        "Validation Failed",
                        422,
                        "One or more fields failed validation"
                )
        );
        body.put("errors", errors);

        return ResponseEntity.status(422)
                .contentType(MediaType.parseMediaType(
                        "application/problem+json;charset=UTF-8"))
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(
            Exception ex) {

        log.error("Unexpected error while processing request", ex);

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal-error",
                "An unexpected error occurred"
        );
    }
}
