package com.example.calendar_scheduler.controllers;

import com.example.calendar_scheduler.dao.Event;
import com.example.calendar_scheduler.dto.EventDTO;
import com.example.calendar_scheduler.services.interfaceServices.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event createdEvent = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<Event>> getEventsByUserAndDate(@PathVariable Long userId, @PathVariable String date) {
        List<Event> events = eventService.getEventsByUserAndDate(userId, date);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/conflicts/{userId}/date/{date}")
    public ResponseEntity<List<Event>> detectConflicts(@PathVariable Long userId, @PathVariable String date) {
        List<Event> conflicts = eventService.detectConflicts(userId, date);
        return ResponseEntity.ok(conflicts);
    }

    @GetMapping("/suggest-time-slots")
    public ResponseEntity<List<String>> suggestTimeSlots(@RequestBody List<Long> userIds, @RequestParam int durationMinutes, @RequestParam String date) {
        List<String> suggestions = eventService.suggestTimeSlots(userIds, durationMinutes, date);
        return ResponseEntity.ok(suggestions);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        List<Event> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(events);
    }
}

