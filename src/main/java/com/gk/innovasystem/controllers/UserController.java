package com.gk.innovasystem.controllers;

import com.gk.innovasystem.dtos.RegisterDTO;
import com.gk.innovasystem.dtos.UpdateRoleDTO;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserEntity> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        UserEntity userData = new UserEntity();
        userData.setName(registerDTO.getName());
        userData.setEmail(registerDTO.getEmail());
        userData.setPassword(registerDTO.getPassword());

        UserEntity registeredUser = userService.register(userData);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userService.findUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        UserEntity user = userService.findUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserEntity> updateUserRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleDTO updateRoleDTO) {
        UserEntity updatedUser = userService.updateUserRole(id, updateRoleDTO.getAdminId(), updateRoleDTO.getRole());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
