package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shopcardd.flashsale.dto.ApiResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handle(RuntimeException ex) {

        HttpStatus status =
                ex.getClass().isAnnotationPresent(ResponseStatus.class)
                        ? ex.getClass().getAnnotation(ResponseStatus.class).value()
                        : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
               new ApiResponse("fail",ex.getMessage()),
                status
        );
    }
}
