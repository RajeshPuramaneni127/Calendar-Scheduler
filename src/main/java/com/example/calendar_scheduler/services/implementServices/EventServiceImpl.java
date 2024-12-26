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

import java.time.Duration;
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

        long timeStamp = ChronoUnit.HOURS.between(eventDTO.getStartTime(),eventDTO.getEndTime());
        if(timeStamp>24){
            throw new InvalidTimeStampException("event should no longer than 24 hourse!!");
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

        List<Event> answer = new ArrayList<>();
        for(Event event: events){
            if(event.getStartTime().isAfter(endOfDay) || (event.getEndDate() != null && event.getEndDate().isBefore(startOfDay))){
                continue;
            }

            long daysBetweenStartAndQuery = ChronoUnit.DAYS.between(event.getStartTime().toLocalDate(),startOfDay);
            long daysBetweenEndAndQuery = ChronoUnit.DAYS.between(event.getEndTime().toLocalDate(),startOfDay);

//            System.out.println(daysBetweenStartAndQuery);
//            System.out.println(daysBetweenStartAndQuery);
            boolean isStartEndSame = daysBetweenStartAndQuery == daysBetweenEndAndQuery;
            List<LocalDateTime> conflicts = new ArrayList<>();
            if(event.getRecurrence() == null || event.getRecurrence() == RecurrenceType.NONE){
                if(daysBetweenStartAndQuery ==0 || (event.getStartTime().isBefore(startOfDay) && event.getEndTime().isAfter(startOfDay))){
                    conflicts.add(event.getStartTime());
                }
            }else{
                switch(event.getRecurrence()){
                    case DAILY : {
                        if (daysBetweenStartAndQuery >= 0 &&
                                (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()))) {
                            conflicts.add(event.getStartTime().plusDays(daysBetweenStartAndQuery));
//                            System.out.println(event.getStartTime());
//                            System.out.println(event.getStartTime().plusDays(daysBetweenStartAndQuery));
//                            System.out.println(event.getStartTime().plusDays(daysBetweenEndAndQuery));
//                            System.out.println(daysBetweenEndAndQuery);
//                            System.out.println();
//                            System.out.println("Yayyyyy working here ");
                        }
                        if(!isStartEndSame && daysBetweenEndAndQuery>=0 && (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()) )){
                            conflicts.add(event.getStartTime().plusDays(daysBetweenEndAndQuery));
//                            System.out.println(event.getStartTime().plusDays(daysBetweenEndAndQuery));
//                            System.out.println("Yayyyyy working here ");
                        }
                        break;
                    }
                    case WEEKLY: {
                        if(daysBetweenStartAndQuery>=0 && daysBetweenStartAndQuery%7 == 0 && (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()))){
                            conflicts.add(event.getStartTime().plusDays(daysBetweenStartAndQuery));
                        } else if(!isStartEndSame && daysBetweenEndAndQuery>=0 && daysBetweenEndAndQuery%7 ==0 && (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()))){
                            conflicts.add(event.getStartTime().plusDays(daysBetweenStartAndQuery));
                        }
                        break;
                    }
                    case MONTHLY: {
                        if (event.getStartTime().getDayOfMonth() == localDate.getDayOfMonth() &&
                                daysBetweenStartAndQuery >= 0 &&
                                (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()))) {
                            conflicts.add(event.getStartTime().plusDays(daysBetweenStartAndQuery));
                        }else if(!isStartEndSame && daysBetweenEndAndQuery>=0 && event.getEndTime().getDayOfMonth() == localDate.getDayOfMonth() && (event.getEndDate() == null || !localDate.isAfter(event.getEndDate().toLocalDate()))){
                            conflicts.add(event.getStartTime().plusDays(daysBetweenStartAndQuery));
                        }
                    }
                    default: break;
                }
            }
            for(LocalDateTime conflict : conflicts){
                Event curr = new Event(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        conflict,
                        conflict.plus(Duration.between(event.getStartTime(),event.getEndTime())),
                        event.getRecurrence(),
                        event.getEndDate(),
                        event.getUsers()
                );
                answer.add(curr);
            }
        }
        if(answer.isEmpty()){
            throw new NoDataFoundException("No events for the user: "+ userId + " on the date : "+ date);
        }
        return answer;

//        List<Event> answer =  events.stream().filter(event -> {
//            if ((event.getStartTime().isAfter(endOfDay)) ||
//                    (event.getEndDate() != null && event.getEndDate().isBefore(startOfDay))) {
//                return false;
//            }
//
//            long daysBetweenStartAndQuery = ChronoUnit.DAYS.between(event.getStartTime(), startOfDay);
//
//            if (event.getRecurrence() == null || event.getRecurrence() == RecurrenceType.NONE) {
//                return daysBetweenStartAndQuery == 0;
//            }
//
//            switch (event.getRecurrence()) {
//                case DAILY:
//                    return daysBetweenStartAndQuery >= 0 &&
//                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));
//
//                case WEEKLY:
//                    return daysBetweenStartAndQuery % 7 == 0 &&
//                            daysBetweenStartAndQuery >= 0 &&
//                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));
//
//                case MONTHLY:
//                    return event.getStartTime().getDayOfMonth() == startOfDay.getDayOfMonth() &&
//                            daysBetweenStartAndQuery >= 0 &&
//                            (event.getEndDate() == null || !startOfDay.isAfter(event.getEndDate()));
//
//                default:
//                    return false;
//            }
//        }).collect(Collectors.toList());
//
//        if(answer.size() ==0 ){
//            throw new NoDataFoundException("No events for the user: " + userId + " on the date : " + date);
//        }
//
//        return answer;
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
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        List<Event> events = getEventsByUserAndDate(userId, date);
        events.sort(Comparator.comparing(event -> ((Event) event).getStartTime())
                .thenComparing(event -> ((Event) event).getEndTime()));


        List<Event> conflicts = new ArrayList<>();
        LocalDateTime prev = events.get(0).getEndTime();
        int prevIndex = 0;
        for (int i = 1; i < events.size(); i++) {
            if(prev.isAfter(events.get(i).getStartTime())){
                conflicts.add(events.get(prevIndex));
                conflicts.add(events.get(i));
//                System.out.print(prev + " " + events.get(i).getStartTime());
//                System.out.println();
            }
            if(!prev.isAfter(events.get(i).getEndTime())){
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


        events.sort(Comparator.comparing(event -> ((Event) event).getStartTime())
                .thenComparing(event -> ((Event) event).getEndTime()));

        List<String> availableSlots = new ArrayList<>();
        LocalDateTime slotStartTime = startOfDay;
        Boolean flag = false;
        for (Event event : events) {
            LocalDateTime eventStartTime = event.getStartTime();
            LocalDateTime eventEndTime = event.getEndTime();

            if (slotStartTime.plusMinutes(durationMinutes).isBefore(eventStartTime)) {
                availableSlots.add(slotStartTime.toLocalTime().toString() + " to " + eventStartTime.toLocalTime().toString());
            }

            if(event.getEndTime().isAfter(endOfDay)){
                flag =true; break;
            }

            slotStartTime = eventEndTime.isAfter(slotStartTime) ? eventEndTime : slotStartTime;
        }


        if (!flag && slotStartTime.plusMinutes(durationMinutes).isBefore(endOfDay)) {
            availableSlots.add(slotStartTime.toLocalTime().toString() + " to " + "24:00");
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