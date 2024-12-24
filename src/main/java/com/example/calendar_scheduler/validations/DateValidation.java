package com.example.calendar_scheduler.validations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidation {
    public DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static boolean isValidDate(String dateTimeString) {
        try {
            LocalDate.parse(dateTimeString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
