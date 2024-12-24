package com.example.calendar_scheduler.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends IllegalArgumentException{
    public UserNotFoundException(String message) { super(message);}
}
