package com.thepointspup.petpolicyapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepointspup.petpolicyapi.model.Poll;
import com.thepointspup.petpolicyapi.model.PollOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PollControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.admin-key}")
    private String apiKey;

    // --- GET /api/polls ---

    @Test
    void getPolls_returnsAllSeedPolls() throws Exception {
        mockMvc.perform(get("/api/polls"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(7))))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].question").exists())
            .andExpect(jsonPath("$[0].options").exists())
            .andExpect(jsonPath("$[0].totalVotes").exists());
    }

    @Test
    void getPolls_eachPollHasAtLeastTwoOptions() throws Exception {
        mockMvc.perform(get("/api/polls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].options.length()", everyItem(greaterThanOrEqualTo(2))));
    }

    // --- GET /api/polls/{id} ---

    @Test
    void getPollById_found() throws Exception {
        mockMvc.perform(get("/api/polls/tabs-vs-spaces"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("tabs-vs-spaces")))
            .andExpect(jsonPath("$.question", is("Tabs or Spaces?")))
            .andExpect(jsonPath("$.options", hasSize(2)))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void getPollById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/polls/nonexistent"))
            .andExpect(status().isNotFound());
    }

    // --- POST /api/polls/{id}/vote ---

    @Test
    void vote_withoutApiKey_succeeds() throws Exception {
        mockMvc.perform(post("/api/polls/tabs-vs-spaces/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("optionKey", "tabs"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.options[?(@.key == 'tabs')].votes", hasItem(greaterThan(0))));
    }

    @Test
    void vote_invalidPollId_returns400() throws Exception {
        mockMvc.perform(post("/api/polls/nonexistent/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("optionKey", "tabs"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void vote_invalidOptionKey_returns400() throws Exception {
        mockMvc.perform(post("/api/polls/tabs-vs-spaces/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("optionKey", "invalid"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void vote_missingOptionKey_returns400() throws Exception {
        mockMvc.perform(post("/api/polls/tabs-vs-spaces/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("optionKey")));
    }

    // --- POST /api/polls (admin) ---

    @Test
    void createPoll_withKey_returnsCreated() throws Exception {
        Poll newPoll = new Poll("integration-test-poll", "Test?", "Test",
            List.of(new PollOption("a", "A"), new PollOption("b", "B")));

        mockMvc.perform(post("/api/polls")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPoll)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is("integration-test-poll")))
            .andExpect(jsonPath("$.lastUpdated").exists());
    }

    @Test
    void createPoll_withoutKey_returns401() throws Exception {
        Poll newPoll = new Poll("nokey-poll", "Test?", "Test",
            List.of(new PollOption("a", "A"), new PollOption("b", "B")));

        mockMvc.perform(post("/api/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPoll)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createPoll_missingId_returns400() throws Exception {
        Poll noId = new Poll();
        noId.setQuestion("Missing ID?");
        noId.setOptions(List.of(new PollOption("a", "A"), new PollOption("b", "B")));

        mockMvc.perform(post("/api/polls")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noId)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("id is required")));
    }

    // --- DELETE /api/polls/{id} (admin) ---

    @Test
    void deletePoll_withKey_returns204() throws Exception {
        Poll toDelete = new Poll("to-delete-poll", "Delete me?", "Test",
            List.of(new PollOption("a", "A"), new PollOption("b", "B")));
        mockMvc.perform(post("/api/polls")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDelete)))
            .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/polls/to-delete-poll")
                .header("X-API-Key", apiKey))
            .andExpect(status().isNoContent());
    }

    @Test
    void deletePoll_withoutKey_returns401() throws Exception {
        mockMvc.perform(delete("/api/polls/tabs-vs-spaces"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deletePoll_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/polls/nonexistent")
                .header("X-API-Key", apiKey))
            .andExpect(status().isNotFound());
    }
}
