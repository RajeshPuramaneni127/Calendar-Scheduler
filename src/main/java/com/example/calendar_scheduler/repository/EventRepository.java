package com.example.calendar_scheduler.repository;

import com.example.calendar_scheduler.dao.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUsers_IdAndStartTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
//    List<Event> findByUserId(Long userId);
    List<Event> findByTitleContainingIgnoreCase(String keyword);
    List<Event> findByUsers_IdAndTitleAndStartTimeAndEndTime(Long userId, String title, LocalDateTime startTime, LocalDateTime endTime);
    @Query("SELECT DISTINCT e FROM Event e " +
            "JOIN e.users u " +
            "WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchEventsByKeyword(@Param("keyword") String keyword);

    List<Event> findByUsers_Id(Long userId);

}


//Retrieve all events for a specific user on a given date.

//Suggest the next available time slot for a set of users, given a specific duration.