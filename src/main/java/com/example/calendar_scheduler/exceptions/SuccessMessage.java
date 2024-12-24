package com.example.calendar_scheduler.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class SuccessMessage extends RuntimeException {
    public SuccessMessage(String message) { super(message); }
}
