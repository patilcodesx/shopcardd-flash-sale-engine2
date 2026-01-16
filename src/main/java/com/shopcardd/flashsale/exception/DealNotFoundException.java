package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DealNotFoundException extends RuntimeException {

    public DealNotFoundException(String message) {
        super(message);
    }
}
