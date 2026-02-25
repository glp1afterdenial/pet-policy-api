package com.thepointspup.petpolicyapi.model;

public class HotelChain {

    private String id;
    private String name;
    private String weightLimit;
    private String fee;
    private boolean breedRestrictions;
    private Integer maxWeightLbs;
    private String notes;
    private String tip;
    private String verdict;
    private String rating;
    private String websiteUrl;

    public HotelChain() {}

    public HotelChain(String id, String name, String weightLimit, String fee,
                      boolean breedRestrictions, Integer maxWeightLbs,
                      String notes, String tip, String verdict, String rating,
                      String websiteUrl) {
        this.id = id;
        this.name = name;
        this.weightLimit = weightLimit;
        this.fee = fee;
        this.breedRestrictions = breedRestrictions;
        this.maxWeightLbs = maxWeightLbs;
        this.notes = notes;
        this.tip = tip;
        this.verdict = verdict;
        this.rating = rating;
        this.websiteUrl = websiteUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWeightLimit() { return weightLimit; }
    public void setWeightLimit(String weightLimit) { this.weightLimit = weightLimit; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }

    public boolean isBreedRestrictions() { return breedRestrictions; }
    public void setBreedRestrictions(boolean breedRestrictions) { this.breedRestrictions = breedRestrictions; }

    public Integer getMaxWeightLbs() { return maxWeightLbs; }
    public void setMaxWeightLbs(Integer maxWeightLbs) { this.maxWeightLbs = maxWeightLbs; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
}
