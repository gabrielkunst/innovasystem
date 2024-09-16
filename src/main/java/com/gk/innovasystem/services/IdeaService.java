package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final EventRepository eventRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository, EventRepository eventRepository) {
        this.ideaRepository = ideaRepository;
        this.eventRepository = eventRepository;
    }

    public List<IdeaEntity> findAllIdeas() {
        return ideaRepository.findAll();
    }

    public IdeaEntity findIdeaById(Long id) {
        return ideaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Idea not found with id: " + id)
        );
    }

    public IdeaEntity createIdea(IdeaEntity ideaEntity) {
        return ideaRepository.save(ideaEntity);
    }

    public IdeaEntity updateIdea(Long id, IdeaEntity ideaEntity) {
        IdeaEntity ideaFromDB = ideaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Idea not found with id: " + id)
        );

        ideaEntity.setId(ideaFromDB.getId());

        return ideaRepository.save(ideaEntity);
    }

    public void deleteIdea(Long id) {
        IdeaEntity ideaFromDB = ideaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Idea not found with id: " + id)
        );

        ideaRepository.delete(ideaFromDB);
    }

    public void distributeIdeas(Long eventId) {
        EventEntity eventFromDB = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found with id: " + eventId)
        );

        List<UserEntity> jury = eventFromDB.getCollaborators();
        List<IdeaEntity> ideas = eventFromDB.getIdeas();

        int jurySize = jury.size();

        if (jurySize < 2) {
            throw new InvalidRequestException("Event must have at least 2 jury members");
        }

        Collections.shuffle(ideas);

        for (IdeaEntity idea: ideas) {
            if (idea.getEvent() == null) {
                idea.setEvent(eventFromDB);
            }

            int juryIndex = ideas.indexOf(idea) % jurySize;
            UserEntity juryMember = jury.get(juryIndex);

            if (idea.getEvent().getCollaborators() == null) {
                idea.getEvent().setCollaborators(new ArrayList<>());
            }

            if (!idea.getEvent().getCollaborators().contains(juryMember)) {
                idea.getEvent().getCollaborators().add(juryMember);
            }

            ideaRepository.save(idea);
        }
    }
}
