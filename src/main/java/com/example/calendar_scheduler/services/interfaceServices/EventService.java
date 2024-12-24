package com.example.calendar_scheduler.services.interfaceServices;

import com.example.calendar_scheduler.dao.Event;
import com.example.calendar_scheduler.dto.EventDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public interface EventService {
    Event createEvent(EventDTO eventDTO);

    //TODO: Retrieve all events for a specific user on a given date.
    List<Event> getEventsByUserAndDate(Long userId, String date);

    //TODO : Identify and return conflicting events for a user on a particular day.
    List<Event> detectConflicts(Long userId, String date);

    //TODO: Suggest the next available time slot for a set of users, given a specific duration.
//    String suggestTimeSlot(List<Long> userIds, int durationMinutes);
    List<String> suggestTimeSlots(List<Long> userIds, int durationMinutes, String date);

    //TODO: Delete an event (Opstional) // Not mentioned in the assignment
    void deleteEvent(Long eventId);

//    List<Event> searchEventsByTitle(String keyword);
//    List<Event> searchEventsByKeyword(String keyword);
      List<Event> searchEvents(String keyword);
}

//Retrieve all events for a specific user on a given date.