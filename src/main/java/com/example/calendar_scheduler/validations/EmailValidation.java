package com.example.calendar_scheduler.validations;

import java.util.regex.Pattern;

public class EmailValidation {
    public static boolean emailValidation(String email) {

        // Regular expression to match valid email formats
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the regex
        Pattern p = Pattern.compile(emailRegex);

        // Check if email matches the pattern
        return email != "" && email != null && p.matcher(email).matches();
    }

}
