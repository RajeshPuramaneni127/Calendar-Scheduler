package com.example.calendar_scheduler.utils;

import java.time.LocalTime;

public class FormatSlot {
    /**
     * Utility method to format the time slot as a string.
     */
    public String formatSlot(LocalTime start, LocalTime end) {
        return start.toString() + " to " + end.toString();
    }
}
