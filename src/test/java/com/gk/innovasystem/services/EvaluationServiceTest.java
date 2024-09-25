package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EvaluationEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EvaluationRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import com.gk.innovasystem.repositories.UserRepository;
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
public class EvaluationServiceTest {

    @Autowired
    private EvaluationService evaluationService;

    @MockBean
    private EvaluationRepository evaluationRepository;

    @MockBean
    private IdeaRepository ideaRepository;

    @MockBean
    private UserRepository userRepository;

    private IdeaEntity idea;
    private UserEntity juror;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        juror = UserEntity.builder().id(1L).build();
        idea = IdeaEntity.builder()
                .id(1L)
                .evaluations(new ArrayList<>())
                .event(EventEntity.builder().collaborators(Collections.singletonList(juror)).build())
                .build();
    }

    @Test
    public void shouldEvaluateIdeaSuccessfully() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findById(1L)).thenReturn(Optional.of(juror));
        when(evaluationRepository.save(any(EvaluationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationEntity evaluation = evaluationService.evaluateIdea(1L, 1L, 8.0);

        assertNotNull(evaluation);
        assertEquals(idea, evaluation.getIdea());
        assertEquals(juror, evaluation.getJuror());
        assertEquals(8.0, evaluation.getScore());
        verify(evaluationRepository).save(evaluation);
    }

    @Test
    public void shouldThrowExceptionWhenIdeaNotFound() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                evaluationService.evaluateIdea(1L, 1L, 8.0));

        assertEquals("Idea not found", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenJurorNotFound() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                evaluationService.evaluateIdea(1L, 1L, 8.0));

        assertEquals("Juror not found", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenJurorCannotEvaluate() {
        UserEntity anotherJuror = UserEntity.builder().id(2L).build();
        idea.getEvent().setCollaborators(Collections.singletonList(anotherJuror));

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findById(1L)).thenReturn(Optional.of(juror));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                evaluationService.evaluateIdea(1L, 1L, 8.0));

        assertEquals("This juror cannot evaluate this idea", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenScoreIsTooLow() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findById(1L)).thenReturn(Optional.of(juror));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                evaluationService.evaluateIdea(1L, 1L, 2.0));

        assertEquals("The score must be between 3 and 10", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenScoreIsTooHigh() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));
        when(userRepository.findById(1L)).thenReturn(Optional.of(juror));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                evaluationService.evaluateIdea(1L, 1L, 11.0));

        assertEquals("The score must be between 3 and 10", exception.getMessage());
    }

    @Test
    public void shouldCalculateAverageScoreSuccessfully() {
        EvaluationEntity eval1 = EvaluationEntity.builder().score(5.0).build();
        EvaluationEntity eval2 = EvaluationEntity.builder().score(7.0).build();
        idea.setEvaluations(List.of(eval1, eval2));

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        Double averageScore = evaluationService.calculateAverageScore(1L);

        assertEquals(6.0, averageScore);
    }

    @Test
    public void shouldThrowExceptionWhenInsufficientEvaluations() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        Exception exception = assertThrows(InvalidRequestException.class, () ->
                evaluationService.calculateAverageScore(1L));

        assertEquals("The idea must be evaluated by 2 jurors", exception.getMessage());
    }
}
