package com.example.calendar_scheduler.services.implementServices;


import com.example.calendar_scheduler.exceptions.*;
import com.example.calendar_scheduler.dao.User;
import com.example.calendar_scheduler.dto.UserDTO;
import com.example.calendar_scheduler.repository.UserRepository;
import com.example.calendar_scheduler.services.interfaceServices.UserService;
import com.example.calendar_scheduler.validations.EmailValidation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional       // don't need mention // by default JPA will make crud operations as transactional // mention it will be helpful later!!
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserDTO userDTO) {
        userDTO.setName(userDTO.getName().trim());
        userDTO.setEmail(userDTO.getEmail().trim());
        if(!EmailValidation.emailValidation(userDTO.getEmail())){
            throw new InvalidInputException("Invalid Email!!");
        }
        if(userDTO.getName()==""){
            throw new InvalidInputException("Name shouldn't be empty!!");
        }
//        if(userDTO.getId() != null && userRepository.findById(userDTO.getId()).isPresent()){
//            throw new UserExistsException("User is already present in the database!!!");
//        }
        if(userRepository.findByEmail(userDTO.getEmail()) != null){
            throw new UserExistsException("User with the same email is already exists in the datase!!!!");
        }
        User user = User.builder()
//                .firstName(userDTO.getFirstName())
//                .lastName(userDTO.getLastName())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void updateUser(Long id, UserDTO userDTO) {
        userDTO.setName(userDTO.getName().trim());
        userDTO.setEmail(userDTO.getEmail().trim());
        if(!userRepository.findById(id).isPresent()){
            throw new UserNotFoundException("User not found!!!!");
        }

        if(!EmailValidation.emailValidation(userDTO.getEmail())){
            throw new InvalidInputException("Invalid Email!!");
        }
        User existingUserWithEmail = userRepository.findByEmail(userDTO.getEmail());
        if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(id)) {
            throw new UserExistsException("Another user with the same email already exists in the database!!");
        }
        User user = getUserById(id);
//        user.setFirstName(userDTO.getFirstName());
//        user.setLastName(userDTO.getLastName());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
//        System.out.println(user.getEmail());
        userRepository.save(user);
//        System.out.println(userRepository.findById(id));
//        throw new SuccessMessage("User Updated :" + id);
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.findById(id).isPresent()){
            throw new UserNotFoundException("User not found!!!!");
        }
        userRepository.deleteById(id);
        throw new SuccessMessage("User Deleted :" + id);
    }


    @Override
    public List<User> searchUsersByName(String keyword) {
        keyword = keyword.trim();
        if(keyword.length()==0){
            throw new EmptyKeywordException("keyword shouldn't be empty!!!");
        }
        List<User> answer =  userRepository.findByNameContainingIgnoreCase(keyword);
        if(answer.size() == 0){
            throw new NoDataFoundException("No users present with the given keyword");
        }
        return answer;
    }
}