package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateEventWhenUserIsAdmin() {
        UserEntity adminUser = UserEntity.builder().id(1L).role(UserEntity.Role.ADMIN).build();
        EventEntity event = EventEntity.builder().id(1L).name("Test Event").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(eventRepository.save(any(EventEntity.class))).thenReturn(event);

        EventEntity createdEvent = eventService.createEvent(event, 1L);

        assertEquals("Test Event", createdEvent.getName());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotAdminForEventCreation() {
        UserEntity nonAdminUser = UserEntity.builder().id(2L).role(UserEntity.Role.VOLUNTEER).build();
        EventEntity event = EventEntity.builder().name("Test Event").build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(nonAdminUser));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> eventService.createEvent(event, 2L)
        );

        assertEquals("User with id 2 is not authorized to create events", exception.getMessage());
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    void shouldFindEventById() {
        EventEntity event = EventEntity.builder().id(1L).name("Test Event").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventEntity foundEvent = eventService.findEventById(1L);

        assertEquals("Test Event", foundEvent.getName());
        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFoundById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findEventById(1L)
        );

        assertEquals("Event with id 1 not found", exception.getMessage());
        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldFindAllEvents() {
        List<EventEntity> events = Arrays.asList(
                EventEntity.builder().id(1L).name("Event 1").build(),
                EventEntity.builder().id(2L).name("Event 2").build()
        );

        when(eventRepository.findAll()).thenReturn(events);

        List<EventEntity> foundEvents = eventService.findAllEvents();

        assertEquals(2, foundEvents.size());
        assertEquals("Event 1", foundEvents.get(0).getName());
        verify(eventRepository).findAll();
    }

    @Test
    void shouldDeleteEventById() {
        EventEntity event = EventEntity.builder().id(1L).name("Test Event").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.deleteEvent(1L)
        );

        assertEquals("Event with id 1 not found", exception.getMessage());
        verify(eventRepository, never()).delete(any(EventEntity.class));
    }

    @Test
    void shouldUpdateEventById() {
        EventEntity existingEvent = EventEntity.builder().id(1L).name("Old Event").build();
        EventEntity updatedEventData = EventEntity.builder().name("Updated Event").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(EventEntity.class))).thenReturn(existingEvent);

        EventEntity updatedEvent = eventService.updateEvent(1L, updatedEventData);

        assertEquals("Updated Event", updatedEvent.getName());
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentEvent() {
        EventEntity updatedEventData = EventEntity.builder().name("Updated Event").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.updateEvent(1L, updatedEventData)
        );

        assertEquals("Event with id 1 not found", exception.getMessage());
        verify(eventRepository, never()).save(any(EventEntity.class));
    }

    @Test
    void shouldSelectJuryWhenUserIsAdmin() {
        EventEntity event = EventEntity.builder().id(1L).build();
        UserEntity adminUser = UserEntity.builder().id(1L).role(UserEntity.Role.ADMIN).build();
        List<UserEntity> jury = Arrays.asList(
                UserEntity.builder().id(2L).role(UserEntity.Role.JUROR).build(),
                UserEntity.builder().id(3L).role(UserEntity.Role.JUROR).build()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepository.findAllById(Arrays.asList(2L, 3L))).thenReturn(jury);
        when(eventRepository.save(any(EventEntity.class))).thenReturn(event);

        EventEntity updatedEvent = eventService.selectJury(1L, 1L, Arrays.asList(2L, 3L));

        assertEquals(jury, updatedEvent.getCollaborators());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldThrowExceptionWhenSelectingJuryAndUserIsNotAdmin() {
        UserEntity nonAdminUser = UserEntity.builder().id(1L).role(UserEntity.Role.VOLUNTEER).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(nonAdminUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(EventEntity.builder().id(1L).build()));

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> eventService.selectJury(1L, 1L, Arrays.asList(2L, 3L))
        );

        assertEquals("User with id 1 is not authorized to select jurors", exception.getMessage());
        verify(eventRepository, never()).save(any(EventEntity.class));
    }
}
