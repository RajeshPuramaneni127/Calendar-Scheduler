package com.example.calendar_scheduler.repository;

import com.example.calendar_scheduler.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContainingIgnoreCase(String keyword);
    User findByEmail(String email);
}


//Fetch user details by their unique identifier.
//Retrieve a list of all users in the system.
//Update user details.
//Delete users from the system.



// TODO: Optimize database queries for efficient data retrieval.


// Abstraction for direct database queries using Spring Data JPA


//.findById(Integer id): allows you to query the database to find an instance of your model by its ID field


//        .findAll(): allows you to retrieve ALL the entries in the database for a given model


//        .save(Person p): allows you to create AND modify instances of your model in the database


//        .delete(Person p): allows you to delete instances of your model from the database