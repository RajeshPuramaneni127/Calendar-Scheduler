package com.example.calendar_scheduler.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.ALREADY_REPORTED)
public class DuplicateEventFound extends IllegalArgumentException{
    public DuplicateEventFound(String message) { super(message); }
}
