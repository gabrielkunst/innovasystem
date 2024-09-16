package com.gk.innovasystem.controllers;

import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    @Autowired
    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    public ResponseEntity<IdeaEntity> createIdea(@RequestBody IdeaEntity ideaEntity) {
        IdeaEntity createdIdea = ideaService.createIdea(ideaEntity);
        return new ResponseEntity<>(createdIdea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdeaEntity> updateIdea(@PathVariable Long id, @RequestBody IdeaEntity ideaEntity) {
        IdeaEntity updatedIdea = ideaService.updateIdea(id, ideaEntity);
        return new ResponseEntity<>(updatedIdea, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdea(@PathVariable Long id) {
        ideaService.deleteIdea(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdeaEntity> findIdeaById(@PathVariable Long id) {
        IdeaEntity idea = ideaService.findIdeaById(id);
        return new ResponseEntity<>(idea, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<IdeaEntity>> findAllIdeas() {
        List<IdeaEntity> ideas = ideaService.findAllIdeas();
        return new ResponseEntity<>(ideas, HttpStatus.OK);
    }

    @PostMapping("/{id}/distribute")
    public ResponseEntity<Void> distributeIdeas(@PathVariable Long id) {
        ideaService.distributeIdeas(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
