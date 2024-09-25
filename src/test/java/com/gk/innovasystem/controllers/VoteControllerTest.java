package com.gk.innovasystem.controllers;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.innovasystem.dtos.VoteDTO;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.VoteService;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    private VoteDTO voteDTO;
    private List<IdeaEntity> topTenIdeas;

    @BeforeEach
    void setUp() {
        voteDTO = new VoteDTO();
        voteDTO.setUserId(1L);
        voteDTO.setEventId(1L);
        voteDTO.setIdeaId(1L);

        IdeaEntity idea1 = new IdeaEntity();
        idea1.setId(1L);
        idea1.setName("Idea 1");
        IdeaEntity idea2 = new IdeaEntity();
        idea2.setId(2L);
        idea2.setName("Idea 2");
        topTenIdeas = Arrays.asList(idea1, idea2);
    }

    @Test
    void vote_Success() throws Exception {
        doNothing().when(voteService).vote(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isCreated());

        verify(voteService).vote(voteDTO.getUserId(), voteDTO.getEventId(), voteDTO.getIdeaId());
    }

    @Test
    void vote_ValidationFailure() throws Exception {
        voteDTO.setUserId(null);

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).vote(anyLong(), anyLong(), anyLong());
    }

    @Test
    void vote_ServiceException() throws Exception {
        doThrow(new RuntimeException("Vote failed")).when(voteService).vote(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isInternalServerError());

        verify(voteService).vote(voteDTO.getUserId(), voteDTO.getEventId(), voteDTO.getIdeaId());
    }

    @Test
    void findTopTenIdeas_Success() throws Exception {
        when(voteService.findTopTenIdeas(anyLong())).thenReturn(topTenIdeas);

        mockMvc.perform(get("/api/v1/votes/1/topTen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Idea 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Idea 2"));

        verify(voteService).findTopTenIdeas(1L);
    }

    @Test
    void findTopTenIdeas_EmptyList() throws Exception {
        when(voteService.findTopTenIdeas(anyLong())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/votes/1/topTen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(voteService).findTopTenIdeas(1L);
    }

    @Test
    void findTopTenIdeas_ServiceException() throws Exception {
        when(voteService.findTopTenIdeas(anyLong())).thenThrow(new RuntimeException("Failed to fetch top ideas"));

        mockMvc.perform(get("/api/v1/votes/1/topTen"))
                .andExpect(status().isInternalServerError());

        verify(voteService).findTopTenIdeas(1L);
    }
}