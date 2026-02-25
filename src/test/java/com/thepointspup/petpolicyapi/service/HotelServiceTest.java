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

    // --- CRUD ---

    @Test
    void createHotel_addsNewChain() {
        HotelChain newHotel = new HotelChain("testchain", "Test Chain", "No limit", "$0", false, null,
            "notes", "tip", "verdict", "good", "https://test.com");
        HotelChain created = service.createHotel(newHotel);

        assertEquals("testchain", created.getId());
        assertNotNull(created.getLastUpdated());
        assertEquals(17, service.getHotels(null, null, null, null).size());
    }

    @Test
    void createHotel_duplicateId_throws() {
        HotelChain dupe = new HotelChain("kimpton", "Dupe", "No limit", "$0", false, null,
            "notes", "tip", "verdict", "good", "https://test.com");
        assertThrows(IllegalArgumentException.class, () -> service.createHotel(dupe));
    }

    @Test
    void updateHotel_replacesAllFields() {
        HotelChain updated = new HotelChain("kimpton", "Kimpton Updated", "50 lbs", "$25", true, 50,
            "new notes", "new tip", "new verdict", "moderate", "https://new.com");
        HotelChain result = service.updateHotel("kimpton", updated);

        assertEquals("Kimpton Updated", result.getName());
        assertEquals("$25", result.getFee());
        assertEquals("moderate", result.getRating());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    void updateHotel_notFound_throws() {
        HotelChain updated = new HotelChain("fake", "Fake", "No limit", "$0", false, null,
            "n", "t", "v", "good", "https://f.com");
        assertThrows(IllegalArgumentException.class, () -> service.updateHotel("fake", updated));
    }

    @Test
    void patchHotel_updatesOnlyProvidedFields() {
        String originalName = service.getHotelById("hilton").get().getName();
        service.patchHotel("hilton", Map.of("fee", "$999"));
        HotelChain patched = service.getHotelById("hilton").get();

        assertEquals("$999", patched.getFee());
        assertEquals(originalName, patched.getName()); // unchanged
        assertNotNull(patched.getLastUpdated());
    }

    @Test
    void patchHotel_unknownField_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            service.patchHotel("hilton", Map.of("badField", "value")));
    }

    @Test
    void patchHotel_notFound_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            service.patchHotel("fake", Map.of("fee", "$0")));
    }

    @Test
    void deleteHotel_removesChain() {
        assertTrue(service.deleteHotel("omni"));
        assertEquals(15, service.getHotels(null, null, null, null).size());
        assertTrue(service.getHotelById("omni").isEmpty());
    }

    @Test
    void deleteHotel_notFound_returnsFalse() {
        assertFalse(service.deleteHotel("nonexistent"));
    }

    // --- compare ---

    @Test
    void compareHotels_returnsRequestedChains() {
        List<HotelChain> result = service.compareHotels(List.of("kimpton", "hilton", "hyatt"));
        assertEquals(3, result.size());
        assertEquals("kimpton", result.get(0).getId());
        assertEquals("hilton", result.get(1).getId());
        assertEquals("hyatt", result.get(2).getId());
    }

    @Test
    void compareHotels_unknownId_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            service.compareHotels(List.of("kimpton", "nonexistent")));
    }

    // --- recommend ---

    @Test
    void recommendHotels_returnsRankedResults() {
        List<HotelChain> result = service.recommendHotels(70, null);
        assertFalse(result.isEmpty());
        // First result should be a top-rated chain
        assertTrue(List.of("excellent", "good").contains(result.get(0).getRating()));
    }

    @Test
    void recommendHotels_withBudget_filtersExpensiveChains() {
        List<HotelChain> all = service.recommendHotels(null, null);
        List<HotelChain> budget = service.recommendHotels(null, 0);
        assertTrue(budget.size() < all.size());
    }

    @Test
    void recommendHotels_noFilters_returnsAllRanked() {
        List<HotelChain> result = service.recommendHotels(null, null);
        assertEquals(16, result.size());
    }

    // --- lastUpdated ---

    @Test
    void allSeedData_hasLastUpdated() {
        service.getHotels(null, null, null, null).forEach(h ->
            assertNotNull(h.getLastUpdated(), h.getId() + " should have lastUpdated"));
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
