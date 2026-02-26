package com.thepointspup.petpolicyapi.controller;

import com.thepointspup.petpolicyapi.model.Poll;
import com.thepointspup.petpolicyapi.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/polls")
@Tag(name = "Tech Polls", description = "Interactive tech debate polls — vote on tabs vs spaces and more")
public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    @Operation(summary = "List all polls", description = "Returns all tech polls with current vote counts")
    public List<Poll> getAllPolls() {
        return pollService.getAllPolls();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get poll by ID", description = "Returns a single poll with its options and vote counts")
    public ResponseEntity<Poll> getPollById(
            @Parameter(description = "Poll ID (e.g. tabs-vs-spaces)")
            @PathVariable String id) {
        return pollService.getPollById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Cast a vote", description = "Vote for an option on a poll. No authentication required.")
    public ResponseEntity<Poll> vote(
            @Parameter(description = "Poll ID")
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String optionKey = body.get("optionKey");
        if (optionKey == null || optionKey.isBlank()) {
            throw new IllegalArgumentException("optionKey is required in request body");
        }
        Poll updated = pollService.vote(id, optionKey);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    @Operation(summary = "Create a poll", description = "Creates a new poll. Requires API key.")
    public ResponseEntity<Poll> createPoll(@RequestBody Poll poll) {
        validatePollBody(poll);
        Poll created = pollService.createPoll(poll);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a poll", description = "Removes a poll. Requires API key.")
    public ResponseEntity<Void> deletePoll(
            @Parameter(description = "Poll ID")
            @PathVariable String id) {
        if (pollService.deletePoll(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private void validatePollBody(Poll poll) {
        if (poll.getId() == null || poll.getId().isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (poll.getQuestion() == null || poll.getQuestion().isBlank()) {
            throw new IllegalArgumentException("question is required");
        }
        if (poll.getOptions() == null || poll.getOptions().size() < 2) {
            throw new IllegalArgumentException("at least 2 options are required");
        }
    }
}
