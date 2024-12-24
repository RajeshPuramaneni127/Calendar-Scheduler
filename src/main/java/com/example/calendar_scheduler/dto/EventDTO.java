package com.example.calendar_scheduler.dto;


import com.example.calendar_scheduler.enums.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private Long id;

    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private RecurrenceType recurrence;
//    private String recurrenceFrequency;
    private LocalDateTime endDate;
    private Set<Long> userIds;

    //TODO: add description
    // private String description;


    // TODO: recurring events
    // private boolean isRecurring;
    // private String recurringFrequency;    // "daily"  or "weekly or "monthly // lets first solve for daily // at the end we will evolve the remaing!!!
}



//Event Management
//
//Implement functionality to:
//Allow users to create "busy time slots" to mark periods when they are unavailable.   // busy time slots means that scheduling a event right!!!
//Retrieve all events for a specific user on a given date.
//Identify and return conflicting events for a user on a particular day.
//Suggest the next available time slot for a set of users, given a specific duration.
// TODO: Create events involving multiple users, specifying the start and end times.



//Hot Features
//Recurring Events: Allow users to create events that repeat over a specified frequency and duration.
//Search Functionality: Enable searching for events by keyword or participant name.
//Advanced Conflict Detection: Add support for partial overlap detection in event conflicts.