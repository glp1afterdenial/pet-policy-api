package com.thepointspup.petpolicyapi.service;

import com.thepointspup.petpolicyapi.data.PollDataLoader;
import com.thepointspup.petpolicyapi.model.Poll;
import com.thepointspup.petpolicyapi.model.PollOption;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PollService {

    private final PollDataLoader dataLoader;

    public PollService(PollDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public List<Poll> getAllPolls() {
        return dataLoader.getAllPolls();
    }

    public Optional<Poll> getPollById(String id) {
        return dataLoader.findById(id);
    }

    public Poll vote(String pollId, String optionKey) {
        Poll poll = dataLoader.findById(pollId)
            .orElseThrow(() -> new IllegalArgumentException("Poll '" + pollId + "' not found"));

        PollOption option = poll.getOptions().stream()
            .filter(o -> o.getKey().equalsIgnoreCase(optionKey))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Option '" + optionKey + "' not found in poll '" + pollId + "'"));

        option.setVotes(option.getVotes() + 1);
        poll.setLastUpdated(LocalDateTime.now());
        return poll;
    }

    public Poll createPoll(Poll poll) {
        if (dataLoader.existsById(poll.getId())) {
            throw new IllegalArgumentException("Poll with id '" + poll.getId() + "' already exists");
        }
        poll.setLastUpdated(LocalDateTime.now());
        dataLoader.add(poll);
        return poll;
    }

    public boolean deletePoll(String id) {
        return dataLoader.removeById(id);
    }
}
