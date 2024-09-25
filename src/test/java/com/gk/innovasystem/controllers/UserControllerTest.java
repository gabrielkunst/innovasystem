package com.gk.innovasystem.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.innovasystem.dtos.RegisterDTO;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.services.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_CreatesUserSuccessfully() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Test User");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");

        UserEntity registeredUser = new UserEntity();
        registeredUser.setId(1L);
        registeredUser.setName(registerDTO.getName());
        registeredUser.setEmail(registerDTO.getEmail());

        Mockito.when(userService.register(any(UserEntity.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_ReturnsUserSuccessfully() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        Mockito.when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserByEmail_ReturnsUserSuccessfully() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        Mockito.when(userService.findUserByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/email/test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("User One");
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setEmail("user2@example.com");

        List<UserEntity> users = Arrays.asList(user1, user2);

        Mockito.when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("User One"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("User Two"));
    }

    @Test
    void updateUserRole_UpdatesRoleSuccessfully() throws Exception {
        UserEntity.Role newRole = UserEntity.Role.ADMIN; // Supondo que você tenha um enum Role na classe UserEntity
        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole(newRole);

        Mockito.when(userService.updateUserRole(eq(1L), any(UserEntity.Role.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value(newRole.name()));
    }
}
