package com.gk.innovasystem.services;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.ResourceAlreadyExistsException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity register(UserEntity userData) {
        UserEntity userFromDB = userRepository.findByEmail(userData.getEmail());

        if (userFromDB != null) {
            throw new ResourceAlreadyExistsException("User with email " + userData.getEmail() + " already exists");
        }

        userData.setRole(UserEntity.Role.VOLUNTEER);
        return userRepository.save(userData);
    }

    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public UserEntity findUserByEmail(String email) {
        UserEntity userFromDB = userRepository.findByEmail(email);

        if (userFromDB == null) {
            throw new ResourceNotFoundException("User with email " + email + " not found");
        }

        return userFromDB;
    }

    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity updateUserRole(Long id, UserEntity.Role role) {
        UserEntity userFromDB = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id " + id + " not found"));

        userFromDB.setRole(role);
        return userRepository.save(userFromDB);
    }
}
