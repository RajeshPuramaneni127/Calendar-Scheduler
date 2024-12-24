package com.example.calendar_scheduler.services.interfaceServices;

import com.example.calendar_scheduler.dao.User;
import com.example.calendar_scheduler.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserService {
    User createUser(UserDTO userDTO);

    User getUserById(Long id);

    List<User> getAllUsers();

    void updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    List<User> searchUsersByName(String keyword);
}

// we need to create a user
// to update the user, we need to fetch
// similarly, to delete the user, we need to fetch!!!

// another query fetching all the users

// services all the business logic

// getting and setting user details