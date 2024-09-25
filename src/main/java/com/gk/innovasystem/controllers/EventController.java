package com.gk.innovasystem.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gk.innovasystem.dtos.CreateEventDTO;
import com.gk.innovasystem.dtos.SelectJuryDTO;
import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.services.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventEntity> createEvent(@Valid @RequestBody CreateEventDTO createEventDTO) {
        EventEntity event = new EventEntity();

        event.setName(createEventDTO.getName());
        event.setDescription(createEventDTO.getDescription());
        event.setStartDate(createEventDTO.getStartDate());
        event.setEndDate(createEventDTO.getEndDate());
        event.setJuryEvaluationStartDate(createEventDTO.getJuryEvaluationStartDate());
        event.setJuryEvaluationEndDate(createEventDTO.getJuryEvaluationEndDate());
        event.setPopularEvaluationStartDate(createEventDTO.getPopularEvaluationStartDate());
        event.setPopularEvaluationEndDate(createEventDTO.getPopularEvaluationEndDate());

        EventEntity createdEvent = eventService.createEvent(event, createEventDTO.getCreatedBy());
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventEntity>> findAllEvents() {
        List<EventEntity> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> findEventById(@PathVariable Long id) {
        EventEntity event = eventService.findEventById(id);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventEntity> updateEvent(@PathVariable Long id, @RequestBody EventEntity event) {
        EventEntity updatedEvent = eventService.updateEvent(id, event);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }

    @PostMapping("/{id}/jury")
    public ResponseEntity<EventEntity> selectJury(@PathVariable Long id, @RequestBody SelectJuryDTO selectJuryDTO) {
        EventEntity event = eventService.selectJury(id, selectJuryDTO.getUserId(), selectJuryDTO.getJuryIds());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

}
