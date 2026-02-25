package com.thepointspup.petpolicyapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- GET /api/hotels ---

    @Test
    void getHotels_returnsAll16Chains() throws Exception {
        mockMvc.perform(get("/api/hotels"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(16)))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].name").exists())
            .andExpect(jsonPath("$[0].fee").exists());
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
            .andExpect(jsonPath("$.fee").exists())
            .andExpect(jsonPath("$.rating", is("excellent")))
            .andExpect(jsonPath("$.websiteUrl").exists());
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
            .andExpect(jsonPath("$.websiteUrl").exists());
    }

    // --- GET /api/stats ---

    @Test
    void getStats_returnsExpectedStructure() throws Exception {
        mockMvc.perform(get("/api/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalChains", is(16)))
            .andExpect(jsonPath("$.noFeeChains").exists())
            .andExpect(jsonPath("$.noWeightLimitChains").exists())
            .andExpect(jsonPath("$.ratingBreakdown").exists())
            .andExpect(jsonPath("$.ratingBreakdown.excellent").exists())
            .andExpect(jsonPath("$.ratingBreakdown.good").exists())
            .andExpect(jsonPath("$.ratingBreakdown.moderate").exists());
    }
}
