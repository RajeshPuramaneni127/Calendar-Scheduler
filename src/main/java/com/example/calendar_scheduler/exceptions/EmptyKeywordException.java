package com.example.calendar_scheduler.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyKeywordException extends RuntimeException{
    public EmptyKeywordException(String message) { super(message); }
}
