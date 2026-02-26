package com.thepointspup.petpolicyapi.model;

public class PollOption {

    private String key;
    private String label;
    private int votes;

    public PollOption() {}

    public PollOption(String key, String label) {
        this.key = key;
        this.label = label;
        this.votes = 0;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
}
