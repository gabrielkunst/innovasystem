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
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.IdeaService;

@WebMvcTest(IdeaController.class)
public class IdeaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdeaService ideaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createIdea_CreatesIdeaSuccessfully() throws Exception {
        IdeaEntity idea = new IdeaEntity();
        idea.setId(1L);
        idea.setName("New Idea");

        Mockito.when(ideaService.createIdea(any(IdeaEntity.class)))
                .thenReturn(idea);

        mockMvc.perform(post("/api/v1/ideas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idea)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Idea"));
    }

    @Test
    void updateIdea_UpdatesIdeaSuccessfully() throws Exception {
        IdeaEntity idea = new IdeaEntity();
        idea.setId(1L);
        idea.setName("Updated Idea");

        Mockito.when(ideaService.updateIdea(eq(1L), any(IdeaEntity.class)))
                .thenReturn(idea);

        mockMvc.perform(put("/api/v1/ideas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(idea)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Idea"));
    }

    @Test
    void deleteIdea_DeletesSuccessfully() throws Exception {
        Mockito.doNothing().when(ideaService).deleteIdea(1L);

        mockMvc.perform(delete("/api/v1/ideas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findIdeaById_ReturnsIdeaSuccessfully() throws Exception {
        IdeaEntity idea = new IdeaEntity();
        idea.setId(1L);
        idea.setName("Test Idea");

        Mockito.when(ideaService.findIdeaById(1L))
                .thenReturn(idea);

        mockMvc.perform(get("/api/v1/ideas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Idea"));
    }

    @Test
    void findAllIdeas_ReturnsListOfIdeas() throws Exception {
        IdeaEntity idea1 = new IdeaEntity();
        idea1.setId(1L);
        idea1.setName("Idea 1");

        IdeaEntity idea2 = new IdeaEntity();
        idea2.setId(2L);
        idea2.setName("Idea 2");

        List<IdeaEntity> ideas = Arrays.asList(idea1, idea2);

        Mockito.when(ideaService.findAllIdeas())
                .thenReturn(ideas);

        mockMvc.perform(get("/api/v1/ideas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Idea 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Idea 2"));
    }

    @Test
    void distributeIdeas_DistributesSuccessfully() throws Exception {
        Mockito.doNothing().when(ideaService).distributeIdeas(1L);

        mockMvc.perform(post("/api/v1/ideas/1/distribute"))
                .andExpect(status().isOk());
    }
}
