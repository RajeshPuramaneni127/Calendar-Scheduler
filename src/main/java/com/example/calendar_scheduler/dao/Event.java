package com.example.calendar_scheduler.dao;


import com.example.calendar_scheduler.enums.RecurrenceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrence;

//    private String recurrenceFrequency;
    private LocalDateTime endDate;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id", nullable = false)
//     private User user;
//
//    // TODO: add Description
//
//
//    // TODO: (additional feature) recurring events : // "Daily" or "Weekly" or "Monthly"
    // private boolean isRecurring;
    // private String recurrenceFrequency;
}
