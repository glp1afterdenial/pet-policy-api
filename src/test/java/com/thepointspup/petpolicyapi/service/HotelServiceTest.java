package com.thepointspup.petpolicyapi.service;

import com.thepointspup.petpolicyapi.data.HotelDataLoader;
import com.thepointspup.petpolicyapi.model.HotelChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HotelServiceTest {

    private HotelService service;

    @BeforeEach
    void setUp() {
        service = new HotelService(new HotelDataLoader());
    }

    @Test
    void getAllHotels_returnsAll16Chains() {
        List<HotelChain> result = service.getHotels(null, null, null, null);
        assertEquals(16, result.size());
    }

    // --- minWeight filter ---

    @Test
    void filterByMinWeight_returnsNoLimitAndAboveThreshold() {
        List<HotelChain> result = service.getHotels(70, null, null, null);
        // Should include chains with no limit (null) and those >= 70
        assertTrue(result.stream().allMatch(h ->
            h.getMaxWeightLbs() == null || h.getMaxWeightLbs() >= 70));
        assertFalse(result.isEmpty());
    }

    @Test
    void filterByMinWeight_excludesBelowThreshold() {
        List<HotelChain> result = service.getHotels(70, null, null, null);
        assertTrue(result.stream().noneMatch(h ->
            h.getMaxWeightLbs() != null && h.getMaxWeightLbs() < 70));
    }

    @Test
    void filterByMinWeight_veryHigh_returnsOnlyNoLimitChains() {
        List<HotelChain> result = service.getHotels(200, null, null, null);
        assertTrue(result.stream().allMatch(h -> h.getMaxWeightLbs() == null));
    }

    @Test
    void filterByMinWeight_zero_returnsAll() {
        List<HotelChain> result = service.getHotels(0, null, null, null);
        assertEquals(16, result.size());
    }

    // --- maxFee filter ---

    @Test
    void filterByMaxFee_zero_returnsFreeChains() {
        List<HotelChain> result = service.getHotels(null, 0, null, null);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(h -> h.getId().equals("kimpton")));
        assertTrue(result.stream().anyMatch(h -> h.getId().equals("redroof")));
    }

    @Test
    void filterByMaxFee_50_includesChainsAtOrBelow50() {
        List<HotelChain> result = service.getHotels(null, 50, null, null);
        assertTrue(result.size() > service.getHotels(null, 0, null, null).size());
    }

    // --- rating filter ---

    @Test
    void filterByRating_excellent_returnsOnlyExcellent() {
        List<HotelChain> result = service.getHotels(null, null, "excellent", null);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(h -> "excellent".equals(h.getRating())));
    }

    @Test
    void filterByRating_good_returnsOnlyGood() {
        List<HotelChain> result = service.getHotels(null, null, "good", null);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(h -> "good".equals(h.getRating())));
    }

    @Test
    void filterByRating_moderate_returnsOnlyModerate() {
        List<HotelChain> result = service.getHotels(null, null, "moderate", null);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(h -> "moderate".equals(h.getRating())));
    }

    @Test
    void filterByRating_caseInsensitive() {
        List<HotelChain> upper = service.getHotels(null, null, "EXCELLENT", null);
        List<HotelChain> lower = service.getHotels(null, null, "excellent", null);
        assertEquals(upper.size(), lower.size());
    }

    // --- search filter ---

    @Test
    void searchByName_findsKimpton() {
        List<HotelChain> result = service.getHotels(null, null, null, "kimpton");
        assertTrue(result.stream().anyMatch(h -> h.getId().equals("kimpton")));
    }

    @Test
    void searchByNotes_findsByContent() {
        List<HotelChain> result = service.getHotels(null, null, null, "gold standard");
        assertTrue(result.stream().anyMatch(h -> h.getId().equals("kimpton")));
    }

    @Test
    void search_caseInsensitive() {
        List<HotelChain> result = service.getHotels(null, null, null, "KIMPTON");
        assertTrue(result.stream().anyMatch(h -> h.getId().equals("kimpton")));
    }

    @Test
    void search_noMatch_returnsEmpty() {
        List<HotelChain> result = service.getHotels(null, null, null, "zzzznonexistent");
        assertTrue(result.isEmpty());
    }

    // --- combined filters ---

    @Test
    void combinedFilters_minWeightAndRating() {
        List<HotelChain> result = service.getHotels(70, null, "excellent", null);
        assertTrue(result.stream().allMatch(h ->
            "excellent".equals(h.getRating()) &&
            (h.getMaxWeightLbs() == null || h.getMaxWeightLbs() >= 70)));
    }

    @Test
    void combinedFilters_maxFeeAndSearch() {
        List<HotelChain> result = service.getHotels(null, 0, null, "kimpton");
        assertEquals(1, result.size());
        assertEquals("kimpton", result.get(0).getId());
    }

    // --- getHotelById ---

    @Test
    void getHotelById_found() {
        Optional<HotelChain> result = service.getHotelById("kimpton");
        assertTrue(result.isPresent());
        assertEquals("Kimpton Hotels", result.get().getName());
    }

    @Test
    void getHotelById_caseInsensitive() {
        Optional<HotelChain> result = service.getHotelById("KIMPTON");
        assertTrue(result.isPresent());
    }

    @Test
    void getHotelById_notFound() {
        Optional<HotelChain> result = service.getHotelById("nonexistent");
        assertTrue(result.isEmpty());
    }

    // --- stats ---

    @Test
    void getStats_returnsTotalChains() {
        Map<String, Object> stats = service.getStats();
        assertEquals(16, stats.get("totalChains"));
    }

    @Test
    void getStats_containsAllExpectedKeys() {
        Map<String, Object> stats = service.getStats();
        assertTrue(stats.containsKey("totalChains"));
        assertTrue(stats.containsKey("noFeeChains"));
        assertTrue(stats.containsKey("noWeightLimitChains"));
        assertTrue(stats.containsKey("ratingBreakdown"));
    }

    @Test
    void getStats_ratingBreakdownSumsToTotal() {
        Map<String, Object> stats = service.getStats();
        @SuppressWarnings("unchecked")
        Map<String, Long> breakdown = (Map<String, Long>) stats.get("ratingBreakdown");
        long sum = breakdown.values().stream().mapToLong(Long::longValue).sum();
        assertEquals((int) stats.get("totalChains"), sum);
    }
}
