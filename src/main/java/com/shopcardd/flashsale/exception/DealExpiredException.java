package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class DealExpiredException extends RuntimeException {

    public DealExpiredException(String message) {
        super(message);
    }
}
