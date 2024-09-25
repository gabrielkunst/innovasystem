package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class IdeaServiceTest {

    @Autowired
    private IdeaService ideaService;

    @MockBean
    private IdeaRepository ideaRepository;

    @MockBean
    private EventRepository eventRepository;

    private IdeaEntity idea;
    private EventEntity event;
    private UserEntity juror1;
    private UserEntity juror2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        juror1 = UserEntity.builder().id(1L).build();
        juror2 = UserEntity.builder().id(2L).build();
        event = EventEntity.builder()
                .id(1L)
                .collaborators(List.of(juror1, juror2))
                .ideas(new ArrayList<>())
                .build();
        idea = IdeaEntity.builder()
                .id(1L)
                .event(event)
                .build();
    }

    @Test
    public void shouldFindAllIdeas() {
        when(ideaRepository.findAll()).thenReturn(List.of(idea));

        List<IdeaEntity> ideas = ideaService.findAllIdeas();

        assertNotNull(ideas);
        assertEquals(1, ideas.size());
        assertEquals(idea, ideas.get(0));
    }

    @Test
    public void shouldFindIdeaById_Success() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        IdeaEntity foundIdea = ideaService.findIdeaById(1L);

        assertNotNull(foundIdea);
        assertEquals(idea, foundIdea);
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenIdeaNotFound() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                ideaService.findIdeaById(1L));

        assertEquals("Idea not found with id: 1", exception.getMessage());
    }

    @Test
    public void shouldCreateIdea_Success() {
        when(ideaRepository.save(any(IdeaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IdeaEntity createdIdea = ideaService.createIdea(idea);

        assertNotNull(createdIdea);
        assertEquals(idea, createdIdea);
        verify(ideaRepository).save(idea);
    }

    @Test
    public void shouldUpdateIdea_Success() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(ideaRepository.save(any(IdeaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IdeaEntity updatedIdea = ideaService.updateIdea(1L, idea);

        assertNotNull(updatedIdea);
        assertEquals(idea.getId(), updatedIdea.getId());
        verify(ideaRepository).save(idea);
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenUpdatingNonExistentIdea() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                ideaService.updateIdea(1L, idea));

        assertEquals("Idea not found with id: 1", exception.getMessage());
    }

    @Test
    public void shouldDeleteIdea_Success() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        ideaService.deleteIdea(1L);

        verify(ideaRepository).delete(idea);
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenDeletingNonExistentIdea() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                ideaService.deleteIdea(1L));

        assertEquals("Idea not found with id: 1", exception.getMessage());
    }

    @Test
    public void shouldDistributeIdeas_Success() {
        event.getIdeas().add(idea);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ideaRepository.save(any(IdeaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ideaService.distributeIdeas(1L);

        verify(ideaRepository).save(idea);
        assertEquals(event, idea.getEvent());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenDistributingIdeasForNonExistentEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                ideaService.distributeIdeas(1L));

        assertEquals("Event not found with id: 1", exception.getMessage());
    }

    @Test
    public void shouldThrowInvalidRequestException_WhenInsufficientJuryMembers() {
        event.setCollaborators(Collections.singletonList(juror1));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                ideaService.distributeIdeas(1L));

        assertEquals("Event must have at least 2 jury members", exception.getMessage());
    }
}
