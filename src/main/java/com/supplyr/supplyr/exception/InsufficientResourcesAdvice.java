package com.supplyr.supplyr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InsufficientResourcesAdvice {

    @ResponseBody
    @ExceptionHandler(InsufficientResourcesException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    String insufficientResourcesHandler(InsufficientResourcesException ex) {
        return ex.getMessage();
    }
}
