package com.thepointspup.petpolicyapi.data;

import com.thepointspup.petpolicyapi.model.Poll;
import com.thepointspup.petpolicyapi.model.PollOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PollDataLoader {

    private final List<Poll> polls = new ArrayList<>(List.of(
        new Poll(
            "tabs-vs-spaces",
            "Tabs or Spaces?",
            "Code Style",
            List.of(new PollOption("tabs", "Tabs"), new PollOption("spaces", "Spaces"))
        ),
        new Poll(
            "dark-vs-light",
            "Dark Mode or Light Mode?",
            "IDE",
            List.of(new PollOption("dark", "Dark Mode"), new PollOption("light", "Light Mode"))
        ),
        new Poll(
            "react-vs-angular",
            "React or Angular?",
            "Frontend",
            List.of(new PollOption("react", "React"), new PollOption("angular", "Angular"))
        ),
        new Poll(
            "semicolons",
            "Semicolons in JavaScript?",
            "Code Style",
            List.of(new PollOption("always", "Always"), new PollOption("never", "Never"))
        ),
        new Poll(
            "git-merge-vs-rebase",
            "Git Merge or Rebase?",
            "Workflow",
            List.of(new PollOption("merge", "Merge"), new PollOption("rebase", "Rebase"))
        ),
        new Poll(
            "monolith-vs-micro",
            "Monolith or Microservices?",
            "Architecture",
            List.of(new PollOption("monolith", "Monolith"), new PollOption("micro", "Microservices"))
        ),
        new Poll(
            "ai-coding",
            "AI-assisted coding: helpful or harmful?",
            "Tools",
            List.of(new PollOption("helpful", "Helpful"), new PollOption("harmful", "Harmful"))
        )
    ));

    public List<Poll> getAllPolls() {
        return polls;
    }

    public Optional<Poll> findById(String id) {
        return polls.stream()
            .filter(p -> p.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    public boolean existsById(String id) {
        return polls.stream().anyMatch(p -> p.getId().equalsIgnoreCase(id));
    }

    public void add(Poll poll) {
        polls.add(poll);
    }

    public boolean removeById(String id) {
        return polls.removeIf(p -> p.getId().equalsIgnoreCase(id));
    }
}
