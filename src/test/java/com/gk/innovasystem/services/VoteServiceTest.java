package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.entities.VoteEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import com.gk.innovasystem.repositories.UserRepository;
import com.gk.innovasystem.repositories.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VoteServiceTest {

    @Autowired
    private VoteService voteService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private VoteRepository voteRepository;

    @MockBean
    private IdeaRepository ideaRepository;

    @MockBean
    private UserRepository userRepository;

    private UserEntity user;
    private EventEntity event;
    private IdeaEntity idea;
    private VoteEntity vote;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = UserEntity.builder().id(1L).role(UserEntity.Role.COLLABORATOR).build();
        event = EventEntity.builder().id(1L).popularEvaluationStartDate(LocalDateTime.now().minusHours(1)).popularEvaluationEndDate(LocalDateTime.now().plusHours(1)).build();
        idea = IdeaEntity.builder().id(1L).build();
        vote = new VoteEntity();
    }

    @Test
    public void shouldVote_Success() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.of(idea));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserIdAndIdeaId(user.getId(), idea.getId())).thenReturn(false);
        when(voteRepository.save(any(VoteEntity.class))).thenReturn(vote);

        voteService.vote(user.getId(), event.getId(), idea.getId());

        verify(voteRepository).save(any(VoteEntity.class));
    }

    @Test
    public void shouldThrowException_WhenEventNotFound() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenIdeaNotFound() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("Idea not found", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenUserNotFound() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.of(idea));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenUserIsNotCollaborator() {
        user.setRole(UserEntity.Role.VOLUNTEER);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.of(idea));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("Only collaborators can vote", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenEvaluationTimeIsOver() {
        event.setPopularEvaluationEndDate(LocalDateTime.now().minusMinutes(1));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.of(idea));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("Evaluation time is over", exception.getMessage());
    }

    @Test
    public void shouldThrowException_WhenUserHasAlreadyVoted() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(ideaRepository.findById(idea.getId())).thenReturn(Optional.of(idea));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserIdAndIdeaId(user.getId(), idea.getId())).thenReturn(true);

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                voteService.vote(user.getId(), event.getId(), idea.getId()));

        assertEquals("User has already voted for this idea", exception.getMessage());
    }

    @Test
    public void shouldFindTopTenIdeas_Success() {
        when(ideaRepository.findByEventId(event.getId())).thenReturn(Collections.singletonList(idea));
        when(voteRepository.countByIdeaId(idea.getId())).thenReturn(5);

        List<IdeaEntity> topIdeas = voteService.findTopTenIdeas(event.getId());

        assertNotNull(topIdeas);
        assertEquals(1, topIdeas.size());
        assertEquals(idea, topIdeas.get(0));
    }

    @Test
    public void shouldReturnEmptyList_WhenNoIdeas() {
        when(ideaRepository.findByEventId(event.getId())).thenReturn(Collections.emptyList());

        List<IdeaEntity> topIdeas = voteService.findTopTenIdeas(event.getId());

        assertNotNull(topIdeas);
        assertTrue(topIdeas.isEmpty());
    }
}
