package com.example.calendar_scheduler.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String firstName;
//
//    private String lastName;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

//    // TODO: Retrieve all events for a specific user on a given date.
//
//     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//     @OrderBy("startTime ASC")
//     private List<Event> events;
}




// Entity Class   // Model class // dao (data access objects) class

// Java Persistance APi (JPA)  // representing data base tables as java objects