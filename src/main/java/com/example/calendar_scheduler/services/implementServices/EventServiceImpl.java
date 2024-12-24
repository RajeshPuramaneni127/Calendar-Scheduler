package com.example.calendar_scheduler.services.implementServices;


import com.example.calendar_scheduler.exceptions.*;
import com.example.calendar_scheduler.dao.Event;
import com.example.calendar_scheduler.dao.User;
import com.example.calendar_scheduler.dto.EventDTO;
import com.example.calendar_scheduler.enums.RecurrenceType;
import com.example.calendar_scheduler.repository.EventRepository;
import com.example.calendar_scheduler.repository.UserRepository;
import com.example.calendar_scheduler.services.interfaceServices.EventService;
import com.example.calendar_scheduler.validations.DateValidation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(EventDTO eventDTO){
        eventDTO.setTitle(eventDTO.getTitle().trim());
        eventDTO.setDescription(eventDTO.getDescription().trim());
        if(eventDTO.getTitle() == null || eventDTO.getTitle().length() == 0){
            throw new InvalidInputException("Title shouldn't be empty!!");
        }
        if(eventDTO.getStartTime() == null || eventDTO.getEndTime() == null || eventDTO.getEndDate() == null){
            throw new InvalidInputException("start time or end time or end date of the event is not mentioned!!");
        }
        if(eventDTO.getRecurrence() == null){
            throw new InvalidInputException("Recurrence type should be mentioned!!");
        }
        if(eventDTO.getUserIds().size() == 0){
            throw new InvalidInputException("Atleast one user should be present in the event!!");
        }
        if(eventDTO.getId() != null && eventRepository.findById(eventDTO.getId()) != null){
            throw new EventExistsException("Event already exists!!!");
        }
        if (eventDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTimeException("Start time must be in the future.");
        }

        if (eventDTO.getEndTime().isBefore(eventDTO.getStartTime())) {
            throw new InvalidTimeStampException("End time must be after start time.");
        }

        if(eventDTO.getEndDate().isBefore(eventDTO.getEndTime())) {
            throw new InvalidTimeStampException("End Date must be same or after End time");
        }

        Set<User> users = eventDTO.getUserIds().stream()
                .map(id -> userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id)))
                .collect(Collectors.toSet());

        for (Long userId : eventDTO.getUserIds()) {
            if (!eventRepository.findByUsers_IdAndTitleAndStartTimeAndEndTime(
                    userId, eventDTO.getTitle(), eventDTO.getStartTime(), eventDTO.getEndTime()).isEmpty()) {
                throw new DuplicateEventFound("Duplicate event detected for user: " + userId);
            }
        }


        Event event = Event.builder()
                .title(eventDTO.getTitle())
                .description(eventDTO.getDescription())
                .startTime(eventDTO.getStartTime())
                .endTime(eventDTO.getEndTime())
                .recurrence(eventDTO.getRecurrence())
                .endDate(eventDTO.getEndDate())
                .users(users)
                .build();

        return eventRepository.save(event);
    }

    @Override
    public List<Event> getEventsByUserAndDate(Long userId, String date) {
        if(!userRepository.findById(userId).isPresent()){
            throw new UserNotFoundException("User not found in the database!!");
        }
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        List<Event> events = eventRepository.findByUsers_Id(userId);

        List<Event> answer =  events.stream().filter(event -> {
            if ((event.getStartTime().isAfter(endOfDay)) ||
                    (event.getEndDate() != null && event.getEndDate().isBefore(startOfDay))) {
                return false;
            }

            long daysBetweenStartAndQuery = ChronoUnit.DAYS.between(event.getStartTime(), startOfDay);

            if (event.getRecurrence() == null || event.getRecurrence() == RecurrenceType.NONE) {
                return daysBetweenStartAndQuery == 0;
            }

            switch (event.getRecurrence()) {
                case DAILY:
                    return daysBetweenStartAndQuery >= 0 &&
                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));

                case WEEKLY:
                    return daysBetweenStartAndQuery % 7 == 0 &&
                            daysBetweenStartAndQuery >= 0 &&
                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));

                case MONTHLY:
                    return event.getStartTime().getDayOfMonth() == startOfDay.getDayOfMonth() &&
                            daysBetweenStartAndQuery >= 0 &&
                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));

                default:
                    return false;
            }
        }).collect(Collectors.toList());

        if(answer.size() ==0 ){
            throw new NoDataFoundException("No events for the user: " + userId + " on the date : " + date);
        }

        return answer;
    }


    @Override
    public List<Event> detectConflicts(Long userId, String date) {

        // TODO: Logic for detecting conflicts of events of a user
        if(!userRepository.findById(userId).isPresent()){
            throw new UserNotFoundException("User not found in the database!!");
        }
        if(!DateValidation.isValidDate(date)){
            throw new InvalidInputException("Invalid Date!!");
        }
        List<Event> events = getEventsByUserAndDate(userId, date);
        events.sort(Comparator.comparing(event -> ((Event) event).getStartTime().toLocalTime())
                .thenComparing(event -> ((Event) event).getEndTime()));


        List<Event> conflicts = new ArrayList<>();
        LocalDateTime prev = events.get(0).getEndTime();
        int prevIndex = 0;
        for (int i = 1; i < events.size(); i++) {
            if(prev.toLocalTime().isAfter(events.get(i).getStartTime().toLocalTime())){
                conflicts.add(events.get(prevIndex));
                conflicts.add(events.get(i));
            }
            if(events.get(i).getStartTime().toLocalTime().isAfter(events.get(i).getEndTime().toLocalTime())){
                ++i;
                while(i<events.size()){
                    conflicts.add(events.get(i));i++;
                }
                break;
            }
            if(!prev.toLocalTime().isAfter(events.get(i).getEndTime().toLocalTime())){
                prev = events.get(i).getEndTime();
                prevIndex = i;
            }
        }
        return conflicts.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> suggestTimeSlots(List<Long> userIds, int durationMinutes, String date) {
        if(userIds.size() == 0){
            throw new InvalidInputException("Atleast one user should be present!!");
        }
        if(!DateValidation.isValidDate(date)){
            throw new InvalidInputException("Invalid Date!!");
        }
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        for(Long userId: userIds){
            if(!userRepository.findById(userId).isPresent()){
                throw new UserNotFoundException("User: "+ userId + " not found in the database!!");
            }
        }

        List<Event> events = userIds.stream()
                .flatMap(userId -> getEventsByUserAndDate(userId, date).stream())
                .distinct()
//                .sorted(Comparator.comparing(event -> event.getStartTime().toLocalTime()))
                .collect(Collectors.toList());


        events.sort(Comparator.comparing(event -> ((Event) event).getStartTime().toLocalTime())
                .thenComparing(event -> ((Event) event).getEndTime()));

        List<String> availableSlots = new ArrayList<>();
        LocalTime slotStartTime = startOfDay.toLocalTime();
        Boolean flag = false;
        for (Event event : events) {
            LocalTime eventStartTime = event.getStartTime().toLocalTime();
            LocalTime eventEndTime = event.getEndTime().toLocalTime();

            if (slotStartTime.plusMinutes(durationMinutes).isBefore(eventStartTime)) {
                availableSlots.add(slotStartTime.toString() + " to " + eventStartTime.toString());
            }

            if(event.getStartTime().toLocalTime().isAfter(event.getEndTime().toLocalTime())){
                flag = true;
                break;
            }

            slotStartTime = eventEndTime.isAfter(slotStartTime) ? eventEndTime : slotStartTime;
        }


        if (!flag && slotStartTime.plusMinutes(durationMinutes).isBefore(endOfDay.toLocalTime())) {
            availableSlots.add(slotStartTime.toString() + " to " + endOfDay.toLocalTime().toString());
        }

        return availableSlots;
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        keyword = keyword.trim();
        if(keyword.isEmpty()){
            throw new EmptyKeywordException("keyword shouldn't be empty!!!");
        }
        List<Event> answer =  eventRepository.searchEventsByKeyword(keyword);
        if(answer.size() == 0){
            throw new NoDataFoundException("No users present with the given keyword");
        }
        return answer;
    }

}



// Updating user
// conflicts is not working
// suggest time slot