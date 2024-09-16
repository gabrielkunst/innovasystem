package com.gk.innovasystem.controllers;

import com.gk.innovasystem.dtos.VoteDTO;
import com.gk.innovasystem.entities.IdeaEntity;
import com.gk.innovasystem.services.VoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping()
    public ResponseEntity<Void> vote(@Valid @RequestBody VoteDTO voteDTO) {
        voteService.vote(voteDTO.getUserId(), voteDTO.getEventId(), voteDTO.getIdeaId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/topTen")
    public ResponseEntity<List<IdeaEntity>> findTopTenIdeas(@PathVariable Long eventId) {
        List<IdeaEntity> topTenIdeas = voteService.findTopTenIdeas(eventId);
        return new ResponseEntity<>(topTenIdeas, HttpStatus.OK);
    }
}
