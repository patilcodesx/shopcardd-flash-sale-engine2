package com.shopcardd.flashsale.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DealSoldOutException extends RuntimeException {
   public DealSoldOutException(String message){
    super(message);
   }
   
}
