package com.thepointspup.petpolicyapi.model;

import java.time.LocalDateTime;
import java.util.List;

public class Poll {

    private String id;
    private String question;
    private String category;
    private List<PollOption> options;
    private LocalDateTime lastUpdated;

    public Poll() {}

    public Poll(String id, String question, String category, List<PollOption> options) {
        this.id = id;
        this.question = question;
        this.category = category;
        this.options = options;
        this.lastUpdated = LocalDateTime.now();
    }

    public int getTotalVotes() {
        if (options == null) return 0;
        return options.stream().mapToInt(PollOption::getVotes).sum();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<PollOption> getOptions() { return options; }
    public void setOptions(List<PollOption> options) { this.options = options; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
