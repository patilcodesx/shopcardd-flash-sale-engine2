package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DealLockedException extends RuntimeException {

    public DealLockedException(String message) {
        super(message);
    }
}
