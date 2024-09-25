package com.gk.innovasystem.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.innovasystem.dtos.EvaluateIdeaDTO;
import com.gk.innovasystem.entities.EvaluationEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.EvaluationService;

@WebMvcTest(EvaluationController.class)
public class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateAverageScore_ReturnsAverage() throws Exception {
        Long ideaId = 1L;
        Double averageScore = 4.5;

        when(evaluationService.calculateAverageScore(ideaId)).thenReturn(averageScore);

        mockMvc.perform(get("/api/v1/evaluations/{ideaId}/average", ideaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(averageScore));
    }

    @Test
void evaluateIdea_CreatesEvaluation() throws Exception {
    EvaluateIdeaDTO evaluateIdeaDTO = new EvaluateIdeaDTO();
    evaluateIdeaDTO.setJurorId(1L);
    evaluateIdeaDTO.setIdeaId(1L);
    evaluateIdeaDTO.setScore(5.0);

    EvaluationEntity evaluationEntity = new EvaluationEntity();
    evaluationEntity.setId(1L);
    evaluationEntity.setScore(5.0);

    when(evaluationService.evaluateIdea(1L, 1L, 5.0)).thenReturn(evaluationEntity);

    mockMvc.perform(post("/api/v1/evaluations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(evaluateIdeaDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.score").value(5));
}


    @Test
    void selectTopIdeas_ReturnsTopIdeas() throws Exception {
        Long ideaId = 1L;
        IdeaEntity idea1 = new IdeaEntity();
        idea1.setId(1L);
        IdeaEntity idea2 = new IdeaEntity();
        idea2.setId(2L);

        when(evaluationService.selectTopIdeas(ideaId)).thenReturn(Arrays.asList(idea1, idea2));

        mockMvc.perform(get("/api/v1/evaluations/{ideaId}/best", ideaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[1].id").value(2L));
    }
}
