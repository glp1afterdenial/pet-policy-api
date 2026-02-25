package com.thepointspup.petpolicyapi.service;

import com.thepointspup.petpolicyapi.data.HotelDataLoader;
import com.thepointspup.petpolicyapi.model.HotelChain;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelDataLoader dataLoader;

    public HotelService(HotelDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public List<HotelChain> getHotels(Integer minWeight, Integer maxFee,
                                       String rating, String search) {
        return dataLoader.getAllHotels().stream()
            .filter(h -> minWeight == null || h.getMaxWeightLbs() == null || h.getMaxWeightLbs() >= minWeight)
            .filter(h -> maxFee == null || extractMinFee(h.getFee()) <= maxFee)
            .filter(h -> rating == null || rating.equalsIgnoreCase(h.getRating()))
            .filter(h -> search == null || matchesSearch(h, search))
            .collect(Collectors.toList());
    }

    public Optional<HotelChain> getHotelById(String id) {
        return dataLoader.findById(id);
    }

    // --- CRUD ---

    public HotelChain createHotel(HotelChain hotel) {
        if (dataLoader.existsById(hotel.getId())) {
            throw new IllegalArgumentException("Hotel chain with id '" + hotel.getId() + "' already exists");
        }
        hotel.setLastUpdated(LocalDateTime.now());
        dataLoader.add(hotel);
        return hotel;
    }

    public HotelChain updateHotel(String id, HotelChain updated) {
        HotelChain existing = dataLoader.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Hotel chain '" + id + "' not found"));

        existing.setName(updated.getName());
        existing.setWeightLimit(updated.getWeightLimit());
        existing.setFee(updated.getFee());
        existing.setBreedRestrictions(updated.isBreedRestrictions());
        existing.setMaxWeightLbs(updated.getMaxWeightLbs());
        existing.setNotes(updated.getNotes());
        existing.setTip(updated.getTip());
        existing.setVerdict(updated.getVerdict());
        existing.setRating(updated.getRating());
        existing.setWebsiteUrl(updated.getWebsiteUrl());
        existing.setLastUpdated(LocalDateTime.now());
        return existing;
    }

    public HotelChain patchHotel(String id, Map<String, Object> fields) {
        HotelChain existing = dataLoader.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Hotel chain '" + id + "' not found"));

        fields.forEach((key, value) -> {
            switch (key) {
                case "name" -> existing.setName((String) value);
                case "weightLimit" -> existing.setWeightLimit((String) value);
                case "fee" -> existing.setFee((String) value);
                case "breedRestrictions" -> existing.setBreedRestrictions((Boolean) value);
                case "maxWeightLbs" -> existing.setMaxWeightLbs(value == null ? null : ((Number) value).intValue());
                case "notes" -> existing.setNotes((String) value);
                case "tip" -> existing.setTip((String) value);
                case "verdict" -> existing.setVerdict((String) value);
                case "rating" -> existing.setRating((String) value);
                case "websiteUrl" -> existing.setWebsiteUrl((String) value);
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        });
        existing.setLastUpdated(LocalDateTime.now());
        return existing;
    }

    public boolean deleteHotel(String id) {
        return dataLoader.removeById(id);
    }

    // --- Compare ---

    public List<HotelChain> compareHotels(List<String> ids) {
        return ids.stream()
            .map(id -> dataLoader.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel chain '" + id + "' not found")))
            .collect(Collectors.toList());
    }

    // --- Recommend ---

    public List<HotelChain> recommendHotels(Integer weight, Integer maxFee) {
        return dataLoader.getAllHotels().stream()
            .filter(h -> weight == null || h.getMaxWeightLbs() == null || h.getMaxWeightLbs() >= weight)
            .filter(h -> maxFee == null || extractMinFee(h.getFee()) <= maxFee)
            .sorted(Comparator.comparingInt(this::scoreHotel).reversed())
            .collect(Collectors.toList());
    }

    private int scoreHotel(HotelChain h) {
        int score = 0;
        // No weight limit is best
        if (h.getMaxWeightLbs() == null) score += 30;
        else if (h.getMaxWeightLbs() >= 75) score += 20;
        else if (h.getMaxWeightLbs() >= 50) score += 10;
        // Lower fees are better
        int fee = extractMinFee(h.getFee());
        if (fee == 0) score += 30;
        else if (fee <= 25) score += 20;
        else if (fee <= 50) score += 15;
        else if (fee <= 75) score += 10;
        // Rating bonus
        switch (h.getRating()) {
            case "excellent" -> score += 30;
            case "good" -> score += 20;
            case "moderate" -> score += 10;
        }
        // No breed restrictions
        if (!h.isBreedRestrictions()) score += 10;
        return score;
    }

    // --- Stats ---

    public Map<String, Object> getStats() {
        List<HotelChain> all = dataLoader.getAllHotels();

        long noFeeCount = all.stream()
            .filter(h -> extractMinFee(h.getFee()) == 0)
            .count();

        long noWeightLimitCount = all.stream()
            .filter(h -> h.getMaxWeightLbs() == null)
            .count();

        long excellentCount = all.stream()
            .filter(h -> "excellent".equals(h.getRating()))
            .count();

        long goodCount = all.stream()
            .filter(h -> "good".equals(h.getRating()))
            .count();

        long moderateCount = all.stream()
            .filter(h -> "moderate".equals(h.getRating()))
            .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalChains", all.size());
        stats.put("noFeeChains", noFeeCount);
        stats.put("noWeightLimitChains", noWeightLimitCount);
        stats.put("ratingBreakdown", Map.of(
            "excellent", excellentCount,
            "good", goodCount,
            "moderate", moderateCount
        ));
        return stats;
    }

    boolean matchesSearch(HotelChain hotel, String search) {
        String lower = search.toLowerCase();
        return hotel.getName().toLowerCase().contains(lower)
            || hotel.getNotes().toLowerCase().contains(lower)
            || hotel.getId().toLowerCase().contains(lower);
    }

    int extractMinFee(String feeString) {
        if (feeString == null) return Integer.MAX_VALUE;
        String lower = feeString.toLowerCase();
        if (lower.contains("free") || lower.startsWith("$0")) return 0;
        Matcher m = Pattern.compile("\\$(\\d+)").matcher(feeString);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return Integer.MAX_VALUE;
    }
}
