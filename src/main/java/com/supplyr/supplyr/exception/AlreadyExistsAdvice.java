package com.supplyr.supplyr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AlreadyExistsAdvice {

    @ResponseBody
    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorDetails alreadyExistsHandler(AlreadyExistsException ex) {
        return new ErrorDetails(HttpStatus.CONFLICT,"AlreadyExistsException",ex.getMessage());
    }
}
