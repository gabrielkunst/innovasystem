package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Autowired
    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public EventEntity createEvent(EventEntity eventData, Long createdBy) {
        UserEntity userFromDB = userRepository.findById(createdBy).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + createdBy + " not found")
        );

        if (!userFromDB.getRole().equals(UserEntity.Role.ADMIN)) {
            throw new InvalidRequestException("User with id " + createdBy + " is not authorized to create events");
        }

        eventData.setCreatedBy(userFromDB);
        return eventRepository.save(eventData);
    }

    public EventEntity findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    public List<EventEntity> findAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEvent(Long id) {
        EventEntity eventFromDB = eventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Event with id " + id + " not found"));

        eventRepository.delete(eventFromDB);
    }

    public EventEntity updateEvent(Long id, EventEntity eventData) {
        EventEntity eventFromDB = eventRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Event with id " + id + " not found"));

        eventFromDB.setName(eventData.getName());
        eventFromDB.setDescription(eventData.getDescription());
        eventFromDB.setStartDate(eventData.getStartDate());
        eventFromDB.setEndDate(eventData.getEndDate());
        eventFromDB.setJuryEvaluationStartDate(eventData.getJuryEvaluationStartDate());
        eventFromDB.setJuryEvaluationEndDate(eventData.getJuryEvaluationEndDate());
        eventFromDB.setPopularEvaluationStartDate(eventData.getPopularEvaluationStartDate());
        eventFromDB.setPopularEvaluationEndDate(eventData.getPopularEvaluationEndDate());

        return eventRepository.save(eventFromDB);
    }

    public EventEntity selectJury(Long eventId, Long userId, List<Long> juryIds) {
        EventEntity eventFromDB = eventRepository.findById(eventId).orElseThrow(() ->
                new ResourceNotFoundException("Event with id " + eventId + " not found"));

        UserEntity userFromDB = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User with id " + userId + " not found"));

        if (!userFromDB.getRole().equals(UserEntity.Role.ADMIN)) {
            throw new InvalidRequestException("User with id " + userId + " is not authorized to select jurors");
        }

        List<UserEntity> jury = userRepository.findAllById(juryIds);

        for (UserEntity juror : jury) {
            if (!juror.getRole().equals(UserEntity.Role.JUROR)) {
                throw new InvalidRequestException("User with id " + juror.getId() + " is not a juror");
            }
        }

        eventFromDB.setCollaborators(jury);
        return eventRepository.save(eventFromDB);
    }
}
