package com.gk.innovasystem.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.gk.innovasystem.exceptions.InvalidRequestException;
import com.gk.innovasystem.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gk.innovasystem.entities.EvaluationEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.entities.UserEntity;
import com.gk.innovasystem.repositories.EvaluationRepository;
import com.gk.innovasystem.repositories.IdeaRepository;
import com.gk.innovasystem.repositories.UserRepository;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    public EvaluationEntity evaluateIdea(Long jurorId, Long ideaId, Double score) {
        IdeaEntity idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found"));

        UserEntity juror = userRepository.findById(jurorId).orElseThrow(
                () -> new ResourceNotFoundException("Juror not found")
        );

        if (!idea.getEvent().getCollaborators().contains(juror)) {
            throw new InvalidRequestException("This juror cannot evaluate this idea");
        }

        if (score < 3 || score > 10) {
            throw new InvalidRequestException("The score must be between 3 and 10");
        }

        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setIdea(idea);
        evaluation.setJuror(juror);
        evaluation.setScore(score);

        return evaluationRepository.save(evaluation);
    }

    public Double calculateAverageScore(Long ideaId) {
        IdeaEntity idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new InvalidRequestException("Idea not found"));

        if (idea.getEvaluations().size() < 2) {
            throw new InvalidRequestException("The idea must be evaluated by 2 jurors");
        }

        return calculateAverage(idea);
    }

    public List<IdeaEntity> selectTopIdeas(Long eventId) {
        List<IdeaEntity> ideas = ideaRepository.findByEventId(eventId);

        ideas.sort(Comparator.comparingDouble(this::calculateAverage).reversed());

        return ideas.stream().limit(10).collect(Collectors.toList());
    }

    private double calculateAverage(IdeaEntity idea) {
        List<EvaluationEntity> evaluations = idea.getEvaluations();

        if (evaluations.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (EvaluationEntity evaluation : evaluations) {
            sum += evaluation.getScore();
        }

        return (double) sum / evaluations.size();
    }
}
