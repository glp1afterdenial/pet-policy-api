package com.thepointspup.petpolicyapi.service;

import com.thepointspup.petpolicyapi.data.PollDataLoader;
import com.thepointspup.petpolicyapi.model.Poll;
import com.thepointspup.petpolicyapi.model.PollOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PollServiceTest {

    private PollService service;

    @BeforeEach
    void setUp() {
        service = new PollService(new PollDataLoader());
    }

    // --- getAllPolls ---

    @Test
    void getAllPolls_returns7SeedPolls() {
        List<Poll> result = service.getAllPolls();
        assertEquals(7, result.size());
    }

    @Test
    void allSeedPolls_haveLastUpdated() {
        service.getAllPolls().forEach(p ->
            assertNotNull(p.getLastUpdated(), p.getId() + " should have lastUpdated"));
    }

    @Test
    void allSeedPolls_haveAtLeastTwoOptions() {
        service.getAllPolls().forEach(p ->
            assertTrue(p.getOptions().size() >= 2, p.getId() + " should have at least 2 options"));
    }

    @Test
    void allSeedPolls_startWithZeroVotes() {
        service.getAllPolls().forEach(p ->
            assertEquals(0, p.getTotalVotes(), p.getId() + " should start with 0 votes"));
    }

    // --- getPollById ---

    @Test
    void getPollById_found() {
        Optional<Poll> result = service.getPollById("tabs-vs-spaces");
        assertTrue(result.isPresent());
        assertEquals("Tabs or Spaces?", result.get().getQuestion());
    }

    @Test
    void getPollById_caseInsensitive() {
        Optional<Poll> result = service.getPollById("TABS-VS-SPACES");
        assertTrue(result.isPresent());
    }

    @Test
    void getPollById_notFound() {
        Optional<Poll> result = service.getPollById("nonexistent");
        assertTrue(result.isEmpty());
    }

    // --- vote ---

    @Test
    void vote_incrementsOptionVoteCount() {
        Poll result = service.vote("tabs-vs-spaces", "tabs");
        PollOption tabs = result.getOptions().stream()
            .filter(o -> o.getKey().equals("tabs")).findFirst().get();
        assertEquals(1, tabs.getVotes());
        assertEquals(1, result.getTotalVotes());
    }

    @Test
    void vote_multipleVotes_accumulate() {
        service.vote("tabs-vs-spaces", "tabs");
        service.vote("tabs-vs-spaces", "tabs");
        Poll result = service.vote("tabs-vs-spaces", "spaces");
        assertEquals(3, result.getTotalVotes());
    }

    @Test
    void vote_updatesLastUpdated() {
        Poll before = service.getPollById("dark-vs-light").get();
        var beforeTime = before.getLastUpdated();
        Poll after = service.vote("dark-vs-light", "dark");
        assertTrue(after.getLastUpdated().compareTo(beforeTime) >= 0);
    }

    @Test
    void vote_invalidPollId_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            service.vote("nonexistent", "tabs"));
    }

    @Test
    void vote_invalidOptionKey_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            service.vote("tabs-vs-spaces", "nonexistent"));
    }

    // --- createPoll ---

    @Test
    void createPoll_addsNewPoll() {
        Poll newPoll = new Poll("test-poll", "Test?", "Test",
            List.of(new PollOption("a", "A"), new PollOption("b", "B")));
        Poll created = service.createPoll(newPoll);

        assertEquals("test-poll", created.getId());
        assertNotNull(created.getLastUpdated());
        assertEquals(8, service.getAllPolls().size());
    }

    @Test
    void createPoll_duplicateId_throws() {
        Poll dupe = new Poll("tabs-vs-spaces", "Dupe?", "Test",
            List.of(new PollOption("a", "A"), new PollOption("b", "B")));
        assertThrows(IllegalArgumentException.class, () -> service.createPoll(dupe));
    }

    // --- deletePoll ---

    @Test
    void deletePoll_removesPoll() {
        assertTrue(service.deletePoll("ai-coding"));
        assertEquals(6, service.getAllPolls().size());
        assertTrue(service.getPollById("ai-coding").isEmpty());
    }

    @Test
    void deletePoll_notFound_returnsFalse() {
        assertFalse(service.deletePoll("nonexistent"));
    }
}
