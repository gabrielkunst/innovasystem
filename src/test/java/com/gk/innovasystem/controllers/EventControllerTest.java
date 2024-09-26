package com.gk.innovasystem.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.innovasystem.dtos.CreateEventDTO;
import com.gk.innovasystem.dtos.SelectJuryDTO;
import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.services.EventService;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventEntity testEvent;
    private CreateEventDTO createEventDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testEvent = new EventEntity();
        testEvent.setId(1L);
        testEvent.setName("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setStartDate(now.plusDays(1));
        testEvent.setEndDate(now.plusDays(2));
        testEvent.setJuryEvaluationStartDate(now.plusDays(3));
        testEvent.setJuryEvaluationEndDate(now.plusDays(4));
        testEvent.setPopularEvaluationStartDate(now.plusDays(5));
        testEvent.setPopularEvaluationEndDate(now.plusDays(6));

        createEventDTO = new CreateEventDTO();
        createEventDTO.setName("Test Event");
        createEventDTO.setDescription("Test Description");
        createEventDTO.setStartDate(now.plusDays(1));
        createEventDTO.setEndDate(now.plusDays(2));
        createEventDTO.setJuryEvaluationStartDate(now.plusDays(3));
        createEventDTO.setJuryEvaluationEndDate(now.plusDays(4));
        createEventDTO.setPopularEvaluationStartDate(now.plusDays(5));
        createEventDTO.setPopularEvaluationEndDate(now.plusDays(6));
        createEventDTO.setCreatedBy(1L);
    }
    @Test
    void createEvent_Success() throws Exception {
        when(eventService.createEvent(any(EventEntity.class), eq(createEventDTO.getCreatedBy())))
            .thenReturn(testEvent);
    
        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEventDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));
    
        verify(eventService).createEvent(any(EventEntity.class), eq(createEventDTO.getCreatedBy()));
    }

    @Test
    void findAllEvents() throws Exception {
        List<EventEntity> events = Arrays.asList(testEvent);
        when(eventService.findAllEvents()).thenReturn(events);

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Event"));

        verify(eventService).findAllEvents();
    }

    @Test
    void findEventById_Success() throws Exception {
        when(eventService.findEventById(1L)).thenReturn(testEvent);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));

        verify(eventService).findEventById(1L);
    }

    @Test
    void findEventById_NotFound() throws Exception {
        when(eventService.findEventById(999L)).thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(get("/api/v1/events/999"))
                .andExpect(status().isInternalServerError());

        verify(eventService).findEventById(999L);
    }

    @Test
    void deleteEvent() throws Exception {
        doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/api/v1/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(1L);
    }

    @Test
    void updateEvent_Success() throws Exception {
        when(eventService.updateEvent(eq(1L), any(EventEntity.class))).thenReturn(testEvent);

        mockMvc.perform(put("/api/v1/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));

        verify(eventService).updateEvent(eq(1L), any(EventEntity.class));
    }

    @Test
    void updateEvent_NotFound() throws Exception {
        when(eventService.updateEvent(eq(999L), any(EventEntity.class))).thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(put("/api/v1/events/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isInternalServerError());

        verify(eventService).updateEvent(eq(999L), any(EventEntity.class));
    }

    @Test
    void selectJury_Success() throws Exception {
        SelectJuryDTO selectJuryDTO = new SelectJuryDTO();
        selectJuryDTO.setUserId(1L);
        selectJuryDTO.setJuryIds(Arrays.asList(2L, 3L, 4L));

        when(eventService.selectJury(eq(1L), eq(1L), anyList())).thenReturn(testEvent);

        mockMvc.perform(post("/api/v1/events/1/jury")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selectJuryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));

        verify(eventService).selectJury(eq(1L), eq(1L), anyList());
    }

    @Test
    void selectJury_EventNotFound() throws Exception {
        SelectJuryDTO selectJuryDTO = new SelectJuryDTO();
        selectJuryDTO.setUserId(1L);
        selectJuryDTO.setJuryIds(Arrays.asList(2L, 3L, 4L));

        when(eventService.selectJury(eq(999L), eq(1L), anyList())).thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(post("/api/v1/events/999/jury")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selectJuryDTO)))
                .andExpect(status().isInternalServerError());

        verify(eventService).selectJury(eq(999L), eq(1L), anyList());
    }
}