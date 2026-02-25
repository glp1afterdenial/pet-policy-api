package com.thepointspup.petpolicyapi.service;

import com.thepointspup.petpolicyapi.data.HotelDataLoader;
import com.thepointspup.petpolicyapi.model.HotelChain;
import org.springframework.stereotype.Service;

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
        return dataLoader.getAllHotels().stream()
            .filter(h -> h.getId().equalsIgnoreCase(id))
            .findFirst();
    }

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

    private boolean matchesSearch(HotelChain hotel, String search) {
        String lower = search.toLowerCase();
        return hotel.getName().toLowerCase().contains(lower)
            || hotel.getNotes().toLowerCase().contains(lower)
            || hotel.getId().toLowerCase().contains(lower);
    }

    private int extractMinFee(String feeString) {
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
