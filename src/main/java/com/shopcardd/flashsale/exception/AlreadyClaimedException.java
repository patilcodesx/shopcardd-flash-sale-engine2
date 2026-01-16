package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class AlreadyClaimedException extends RuntimeException {

    public AlreadyClaimedException(String message) {
        super(message);
    }
}
