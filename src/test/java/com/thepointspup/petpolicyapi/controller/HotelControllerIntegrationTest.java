package com.thepointspup.petpolicyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepointspup.petpolicyapi.model.HotelChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.admin-key}")
    private String apiKey;

    // --- GET /api/hotels ---

    @Test
    void getHotels_returnsAllChains() throws Exception {
        mockMvc.perform(get("/api/hotels"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(16))))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].name").exists())
            .andExpect(jsonPath("$[0].lastUpdated").exists());
    }

    @Test
    void getHotels_filterByMinWeight() throws Exception {
        mockMvc.perform(get("/api/hotels").param("minWeight", "70"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[?(@.maxWeightLbs != null && @.maxWeightLbs < 70)]").isEmpty());
    }

    @Test
    void getHotels_filterByMaxFee_zero() throws Exception {
        mockMvc.perform(get("/api/hotels").param("maxFee", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[?(@.id == 'kimpton')]").exists())
            .andExpect(jsonPath("$[?(@.id == 'redroof')]").exists());
    }

    @Test
    void getHotels_filterByRating() throws Exception {
        mockMvc.perform(get("/api/hotels").param("rating", "excellent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[*].rating", everyItem(is("excellent"))));
    }

    @Test
    void getHotels_searchByName() throws Exception {
        mockMvc.perform(get("/api/hotels").param("search", "kimpton"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[?(@.id == 'kimpton')]").exists());
    }

    @Test
    void getHotels_combinedFilters() throws Exception {
        mockMvc.perform(get("/api/hotels")
                .param("minWeight", "50")
                .param("rating", "good"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].rating", everyItem(is("good"))));
    }

    @Test
    void getHotels_noResults() throws Exception {
        mockMvc.perform(get("/api/hotels").param("search", "zzzznonexistent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- Validation: 400 errors ---

    @Test
    void getHotels_negativeMinWeight_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels").param("minWeight", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("minWeight")));
    }

    @Test
    void getHotels_negativeMaxFee_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels").param("maxFee", "-5"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("maxFee")));
    }

    @Test
    void getHotels_invalidRating_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels").param("rating", "fantastic"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("rating")));
    }

    @Test
    void getHotels_nonNumericMinWeight_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels").param("minWeight", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    // --- GET /api/hotels/{id} ---

    @Test
    void getHotelById_found() throws Exception {
        mockMvc.perform(get("/api/hotels/kimpton"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("kimpton")))
            .andExpect(jsonPath("$.name", is("Kimpton Hotels")))
            .andExpect(jsonPath("$.rating", is("excellent")))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void getHotelById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/hotels/nonexistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getHotelById_allFieldsPresent() throws Exception {
        mockMvc.perform(get("/api/hotels/hilton"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.weightLimit").exists())
            .andExpect(jsonPath("$.fee").exists())
            .andExpect(jsonPath("$.breedRestrictions").exists())
            .andExpect(jsonPath("$.notes").exists())
            .andExpect(jsonPath("$.tip").exists())
            .andExpect(jsonPath("$.verdict").exists())
            .andExpect(jsonPath("$.rating").exists())
            .andExpect(jsonPath("$.websiteUrl").exists())
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    // --- API Key: 401 for write ops without key ---

    @Test
    void writeWithoutApiKey_returns401() throws Exception {
        HotelChain hotel = new HotelChain("nokey", "No Key", "No limit", "$0", false, null,
            "n", "t", "v", "good", "https://test.com");

        mockMvc.perform(post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hotel)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void writeWithBadApiKey_returns401() throws Exception {
        mockMvc.perform(delete("/api/hotels/kimpton")
                .header("X-API-Key", "wrong-key"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getEndpoints_workWithoutApiKey() throws Exception {
        mockMvc.perform(get("/api/hotels"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/stats"))
            .andExpect(status().isOk());
    }

    // --- POST /api/hotels (with key) ---

    @Test
    void createHotel_withKey_returnsCreated() throws Exception {
        HotelChain newHotel = new HotelChain("testcreate", "Test Create Hotel", "No limit", "$0", false, null,
            "Test notes", "Test tip", "Test verdict", "good", "https://test.com");

        mockMvc.perform(post("/api/hotels")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newHotel)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is("testcreate")))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void createHotel_duplicateId_returns400() throws Exception {
        HotelChain dupe = new HotelChain("kimpton", "Dupe", "No limit", "$0", false, null,
            "n", "t", "v", "good", "https://test.com");

        mockMvc.perform(post("/api/hotels")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dupe)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void createHotel_missingId_returns400() throws Exception {
        HotelChain noId = new HotelChain();
        noId.setName("No ID Hotel");

        mockMvc.perform(post("/api/hotels")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noId)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("id is required")));
    }

    @Test
    void createHotel_missingName_returns400() throws Exception {
        HotelChain noName = new HotelChain();
        noName.setId("noname");

        mockMvc.perform(post("/api/hotels")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noName)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("name is required")));
    }

    // --- PUT /api/hotels/{id} (with key) ---

    @Test
    void updateHotel_withKey_replacesData() throws Exception {
        HotelChain updated = new HotelChain("hilton", "Hilton Updated", "100 lbs", "$75", false, 100,
            "Updated notes", "Updated tip", "Updated verdict", "excellent", "https://hilton-updated.com");

        mockMvc.perform(put("/api/hotels/hilton")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Hilton Updated")))
            .andExpect(jsonPath("$.fee", is("$75")))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void updateHotel_notFound_returns400() throws Exception {
        HotelChain updated = new HotelChain("fake", "Fake", "No limit", "$0", false, null,
            "n", "t", "v", "good", "https://f.com");

        mockMvc.perform(put("/api/hotels/fake")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    // --- PATCH /api/hotels/{id} (with key) ---

    @Test
    void patchHotel_withKey_updatesPartially() throws Exception {
        mockMvc.perform(patch("/api/hotels/marriott")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("fee", "$999"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fee", is("$999")))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void patchHotel_invalidRating_returns400() throws Exception {
        mockMvc.perform(patch("/api/hotels/marriott")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("rating", "bad"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("rating")));
    }

    // --- DELETE /api/hotels/{id} (with key) ---

    @Test
    void deleteHotel_withKey_returns204() throws Exception {
        HotelChain toDelete = new HotelChain("todelete", "To Delete", "No limit", "$0", false, null,
            "n", "t", "v", "good", "https://d.com");
        mockMvc.perform(post("/api/hotels")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDelete)))
            .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/hotels/todelete")
                .header("X-API-Key", apiKey))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteHotel_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/hotels/nonexistent")
                .header("X-API-Key", apiKey))
            .andExpect(status().isNotFound());
    }

    // --- GET /api/hotels/compare ---

    @Test
    void compareHotels_returnsSideBySide() throws Exception {
        mockMvc.perform(get("/api/hotels/compare").param("ids", "kimpton", "hilton", "hyatt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is("kimpton")))
            .andExpect(jsonPath("$[1].id", is("hilton")))
            .andExpect(jsonPath("$[2].id", is("hyatt")));
    }

    @Test
    void compareHotels_unknownId_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels/compare").param("ids", "kimpton", "nonexistent"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void compareHotels_tooMany_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels/compare")
                .param("ids", "kimpton", "hilton", "hyatt", "marriott", "ihg", "wyndham"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("between 1 and 5")));
    }

    // --- GET /api/hotels/recommend ---

    @Test
    void recommendHotels_returnsRankedList() throws Exception {
        mockMvc.perform(get("/api/hotels/recommend").param("weight", "70"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void recommendHotels_withBudget() throws Exception {
        mockMvc.perform(get("/api/hotels/recommend")
                .param("weight", "50")
                .param("maxFee", "25"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void recommendHotels_negativeWeight_returns400() throws Exception {
        mockMvc.perform(get("/api/hotels/recommend").param("weight", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("weight")));
    }

    // --- GET /api/stats ---

    @Test
    void getStats_returnsExpectedStructure() throws Exception {
        mockMvc.perform(get("/api/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalChains").exists())
            .andExpect(jsonPath("$.noFeeChains").exists())
            .andExpect(jsonPath("$.noWeightLimitChains").exists())
            .andExpect(jsonPath("$.ratingBreakdown").exists())
            .andExpect(jsonPath("$.ratingBreakdown.excellent").exists())
            .andExpect(jsonPath("$.ratingBreakdown.good").exists())
            .andExpect(jsonPath("$.ratingBreakdown.moderate").exists());
    }
}
