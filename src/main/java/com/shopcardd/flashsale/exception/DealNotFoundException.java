package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class DealNotFoundException extends RuntimeException {

    public DealNotFoundException(String message) {
        super(message);
    }
}
