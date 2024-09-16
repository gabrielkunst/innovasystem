package com.gk.innovasystem.controllers;

import com.gk.innovasystem.dtos.EvaluateIdeaDTO;
import com.gk.innovasystem.entities.EvaluationEntity;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("{ideaId}/average")
    public ResponseEntity<Double> calculateAverageScore(@PathVariable Long ideaId) {
        Double averageRating = evaluationService.calculateAverageScore(ideaId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<EvaluationEntity> evaluateIdea(@Valid @RequestBody EvaluateIdeaDTO evaluateIdeaDTO) {
        EvaluationEntity evaluation = evaluationService.evaluateIdea(
                evaluateIdeaDTO.getJurorId(),
                evaluateIdeaDTO.getIdeaId(),
                evaluateIdeaDTO.getScore()
        );

        return new ResponseEntity<>(evaluation, HttpStatus.CREATED);
    }

    @GetMapping("{ideaId}/best")
    public ResponseEntity<List<IdeaEntity>> selectTopIdeas(@PathVariable Long ideaId) {
        List<IdeaEntity> ideas = evaluationService.selectTopIdeas(ideaId);
        return new ResponseEntity<>(ideas, HttpStatus.OK);
    }
}
