package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceAlreadyExistsException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserEntity user;
    private UserEntity admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = UserEntity.builder().id(1L).email("user@example.com").role(UserEntity.Role.VOLUNTEER).build();
        admin = UserEntity.builder().id(2L).email("admin@example.com").role(UserEntity.Role.ADMIN).build();
    }

    @Test
    public void shouldRegisterUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity registeredUser = userService.register(user);

        assertNotNull(registeredUser);
        assertEquals(user, registeredUser);
        verify(userRepository).save(user);
    }

    @Test
    public void shouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () ->
                userService.register(user));

        assertEquals("User with email " + user.getEmail() + " already exists", exception.getMessage());
    }

    @Test
    public void shouldFindUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity userFromDB = userService.findUserById(1L);

        assertNotNull(userFromDB);
        assertEquals(user, userFromDB);
    }

    @Test
    public void shouldThrowException_WhenUserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.findUserById(1L));

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    public void shouldFindUserByEmail_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserEntity userFromDB = userService.findUserByEmail(user.getEmail());

        assertNotNull(userFromDB);
        assertEquals(user, userFromDB);
    }

    @Test
    public void shouldThrowException_WhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.findUserByEmail(user.getEmail()));

        assertEquals("User with email " + user.getEmail() + " not found", exception.getMessage());
    }

    @Test
    public void shouldFindAllUsers_Success() {
        List<UserEntity> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> usersFromDB = userService.findAllUsers();

        assertEquals(users, usersFromDB);
    }

    @Test
    public void shouldUpdateUserRole_Success() {
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity updatedUser = userService.updateUserRole(user.getId(), admin.getId(), UserEntity.Role.ADMIN);

        assertEquals(UserEntity.Role.ADMIN, updatedUser.getRole());
        verify(userRepository).save(user);
    }

    @Test
    public void shouldThrowException_WhenAdminNotFound() {
        when(userRepository.findById(admin.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserRole(user.getId(), admin.getId(), UserEntity.Role.ADMIN));

        assertEquals("Admin with id " + admin.getId() + " not found", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenUserNotFoundForRoleUpdate() {
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserRole(user.getId(), admin.getId(), UserEntity.Role.ADMIN));

        assertEquals("User with id " + user.getId() + " not found", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenUserIsNotAdmin() {
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                userService.updateUserRole(user.getId(), admin.getId(), UserEntity.Role.ADMIN));

        assertEquals("Only admins can update user roles", exception.getMessage());
    }
}
