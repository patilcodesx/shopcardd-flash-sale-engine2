package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handle(RuntimeException ex) {

        HttpStatus status =
                ex.getClass().isAnnotationPresent(ResponseStatus.class)
                        ? ex.getClass().getAnnotation(ResponseStatus.class).value()
                        : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                Map.of("message", ex.getMessage()),
                status
        );
    }
}
