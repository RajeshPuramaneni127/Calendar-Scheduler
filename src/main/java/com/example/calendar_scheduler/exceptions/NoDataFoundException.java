package com.example.calendar_scheduler.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class NoDataFoundException extends RuntimeException{
    public NoDataFoundException(String message) { super(message); }
}
