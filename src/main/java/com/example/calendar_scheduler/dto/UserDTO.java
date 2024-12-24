package com.example.calendar_scheduler.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
//    private String firstName;
//    private String lastName;
    private String name;
    private String email;
}



// Data transfer objects (dto)
//


//User Management
//
//Implement functionality to:
//Add new users with unique identifiers and basic details such as name and email etc.
//Fetch user details by their unique identifier.
//Retrieve a list of all users in the system.
//Update user details.
//Delete users from the system.
































// user email shouldn't be empty string
// user name shouldn't be empty string
// user email should be validated


// update user API is not working !!!

// DELETE user API is not working!!!


// finding user with keyword (status code should be changed)



// creating event API is not working (The given id must not be null)

// get the events of a user on a particular date!!

// check if the user is not present in database or not in get suggest time slot

// space shouldn't be present from front and back









