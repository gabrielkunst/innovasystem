package com.gk.innovasystem.controllers;

import static org.mockito.Mockito.*;
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
import com.gk.innovasystem.dtos.SelectJuryDTO;
import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.services.EventService;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;
/*
    @Test
    void createEvent_CreatesEventSuccessfully() throws Exception {

        CreateEventDTO createEventDTO = new CreateEventDTO();
        createEventDTO.setName("New Event");
        createEventDTO.setDescription("Event Description");
        createEventDTO.setStartDate(LocalDate.now().atStartOfDay());
        createEventDTO.setEndDate(LocalDate.now().plusDays(5).atStartOfDay());
        createEventDTO.setJuryEvaluationStartDate(LocalDate.now().plusDays(6).atStartOfDay());
        createEventDTO.setJuryEvaluationEndDate(LocalDate.now().plusDays(10).atStartOfDay());
        createEventDTO.setPopularEvaluationStartDate(LocalDate.now().plusDays(11).atStartOfDay());
        createEventDTO.setPopularEvaluationEndDate(LocalDate.now().plusDays(15).atStartOfDay());
        createEventDTO.setCreatedBy(1L);

        EventEntity createdEvent = new EventEntity();
        createdEvent.setId(1L);
        createdEvent.setName(createEventDTO.getName());
        createdEvent.setDescription(createEventDTO.getDescription());

        when(eventService.createEvent(any(EventEntity.class), eq(1L)))
            .thenReturn(createdEvent);

        System.out.println("Request payload: " + objectMapper.writeValueAsString(createEventDTO));

        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEventDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Event"))
                .andExpect(jsonPath("$.description").value("Event Description"));
    }
 */
    @Test
    void findAllEvents_ReturnsEventList() throws Exception {
        EventEntity event1 = new EventEntity();
        event1.setId(1L);
        event1.setName("Event 1");

        EventEntity event2 = new EventEntity();
        event2.setId(2L);
        event2.setName("Event 2");

        List<EventEntity> eventList = Arrays.asList(event1, event2);

        when(eventService.findAllEvents()).thenReturn(eventList);

        mockMvc.perform(get("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Event 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Event 2"));
    }

    @Test
    void findEventById_ReturnsEvent() throws Exception {
        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setName("Test Event");

        when(eventService.findEventById(1L)).thenReturn(event);

        mockMvc.perform(get("/api/v1/events/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void deleteEvent_DeletesSuccessfully() throws Exception {
        Mockito.doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/api/v1/events/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void selectJury_SelectsJurySuccessfully() throws Exception {
        SelectJuryDTO selectJuryDTO = new SelectJuryDTO();
        selectJuryDTO.setUserId(1L);
        selectJuryDTO.setJuryIds(Arrays.asList(2L, 3L));

        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setName("Event with Jury");

        when(eventService.selectJury(1L, 1L, Arrays.asList(2L, 3L)))
                .thenReturn(event);

        mockMvc.perform(post("/api/v1/events/1/jury")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selectJuryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Event with Jury"));
    }
}
