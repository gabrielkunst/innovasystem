package com.gk.innovasystem.controllers;

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
import com.gk.innovasystem.dtos.VoteDTO;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.VoteService;

@WebMvcTest(VoteController.class)
public class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void vote_CreatesVoteSuccessfully() throws Exception {
        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setUserId(1L);
        voteDTO.setEventId(1L);
        voteDTO.setIdeaId(1L);

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isCreated());
        
        Mockito.verify(voteService).vote(1L, 1L, 1L);
    }

    @Test
    void findTopTenIdeas_ReturnsTopTenIdeasSuccessfully() throws Exception {
        IdeaEntity idea1 = new IdeaEntity();
        idea1.setId(1L);
        idea1.setName("Idea One");

        IdeaEntity idea2 = new IdeaEntity();
        idea2.setId(2L);
        idea2.setName("Idea Two");

        List<IdeaEntity> topIdeas = Arrays.asList(idea1, idea2);

        Mockito.when(voteService.findTopTenIdeas(1L)).thenReturn(topIdeas);

        mockMvc.perform(get("/api/v1/votes/1/topTen")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Idea One"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Idea Two"));
    }
/*
    @Test
    void vote_WhenVoteFails_ReturnsBadRequest() throws Exception {
        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setUserId(1L);
        voteDTO.setEventId(1L);
        voteDTO.setIdeaId(1L);

        Mockito.doThrow(new RuntimeException("Vote failed")).when(voteService).vote(any(Long.class), any(Long.class), any(Long.class));

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Vote failed"));
    }
                 */
}
