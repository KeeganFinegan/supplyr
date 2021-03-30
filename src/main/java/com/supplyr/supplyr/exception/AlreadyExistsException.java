package com.supplyr.supplyr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


public class AlreadyExistsException extends RuntimeException{

    public AlreadyExistsException(String message){
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }

}
