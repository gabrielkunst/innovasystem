package com.gk.innovasystem.services;

import com.gk.innovasystem.entities.EventEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.entities.VoteEntity;
import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import com.gk.innovasystem.repositories.EventRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import com.gk.innovasystem.repositories.UserRepository;
import com.gk.innovasystem.repositories.VoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final EventRepository eventRepository;
    private final VoteRepository voteRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    public VoteService(EventRepository eventRepository, VoteRepository voteRepository, IdeaRepository ideaRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.voteRepository = voteRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
    }



    public void vote(Long userId, Long eventId, Long ideaId) {
        EventEntity eventFromDB = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        IdeaEntity ideaFromDB = ideaRepository.findById(ideaId).orElseThrow(() -> new ResourceNotFoundException("Idea not found"));
        UserEntity userFromDB = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        UserEntity.Role userRole = userFromDB.getRole();

        if (!userRole.equals(UserEntity.Role.COLLABORATOR)){
            throw new InvalidRequestException("Only collaborators can vote");
        }

        boolean isValidTimeRange = now.isAfter(eventFromDB.getPopularEvaluationStartDate()) && now.isBefore(eventFromDB.getPopularEvaluationEndDate());

        if (!isValidTimeRange){
            throw new InvalidRequestException("Evaluation time is over");
        }

        boolean hasUserVoted = voteRepository.existsByUserIdAndIdeaId(userId, ideaId);

        if (hasUserVoted){
            throw new InvalidRequestException("User has already voted for this idea");
        }

        VoteEntity vote = new VoteEntity();
        vote.setIdea(ideaFromDB);
        vote.setUser(userFromDB);
        vote.setEvent(eventFromDB);

        voteRepository.save(vote);
    }

    public List<IdeaEntity> findTopTenIdeas(Long eventId) {
        List<IdeaEntity> ideas = ideaRepository.findByEventId(eventId);

        ideas.sort(Comparator.comparingDouble(this::calculateScore).reversed());

        return ideas.stream().limit(10).collect(Collectors.toList());
    }

    private double calculateScore(IdeaEntity idea) {
        return voteRepository.countByIdeaId(idea.getId());
    }
}
