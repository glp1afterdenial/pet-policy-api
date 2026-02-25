package com.thepointspup.petpolicyapi.controller;

import com.thepointspup.petpolicyapi.model.HotelChain;
import com.thepointspup.petpolicyapi.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Hotel Pet Policies", description = "Pet policy data for major hotel chains")
public class HotelController {

    private static final List<String> VALID_RATINGS = List.of("excellent", "good", "moderate");

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // --- Read ---

    @GetMapping("/hotels")
    @Operation(summary = "List hotel chains", description = "Returns all hotel chains with optional filtering by weight, fee, rating, or search term. Filters combine with AND logic.")
    public List<HotelChain> getHotels(
            @Parameter(description = "Minimum dog weight in lbs — returns chains allowing dogs at or above this weight")
            @RequestParam(required = false) Integer minWeight,
            @Parameter(description = "Maximum pet fee in dollars — returns chains with fees at or below this amount")
            @RequestParam(required = false) Integer maxFee,
            @Parameter(description = "Rating filter: excellent, good, or moderate")
            @RequestParam(required = false) String rating,
            @Parameter(description = "Search by chain name or notes (case-insensitive)")
            @RequestParam(required = false) String search) {
        validateMinWeight(minWeight);
        validateMaxFee(maxFee);
        validateRating(rating);
        return hotelService.getHotels(minWeight, maxFee, rating, search);
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get hotel chain by ID", description = "Returns a single hotel chain's full pet policy details")
    public ResponseEntity<HotelChain> getHotelById(
            @Parameter(description = "Chain ID (e.g. kimpton, hilton, marriott)")
            @PathVariable String id) {
        return hotelService.getHotelById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // --- Create ---

    @PostMapping("/hotels")
    @Operation(summary = "Add a hotel chain", description = "Creates a new hotel chain entry. The id must be unique.")
    public ResponseEntity<HotelChain> createHotel(@RequestBody HotelChain hotel) {
        validateHotelBody(hotel);
        HotelChain created = hotelService.createHotel(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // --- Update ---

    @PutMapping("/hotels/{id}")
    @Operation(summary = "Replace a hotel chain", description = "Fully replaces an existing hotel chain's data. All fields are overwritten.")
    public ResponseEntity<HotelChain> updateHotel(
            @PathVariable String id,
            @RequestBody HotelChain hotel) {
        validateHotelBody(hotel);
        return ResponseEntity.ok(hotelService.updateHotel(id, hotel));
    }

    @PatchMapping("/hotels/{id}")
    @Operation(summary = "Partially update a hotel chain", description = "Updates only the provided fields. Useful for changing just the fee or weight limit.")
    public ResponseEntity<HotelChain> patchHotel(
            @PathVariable String id,
            @RequestBody Map<String, Object> fields) {
        if (fields.containsKey("rating")) {
            String rating = (String) fields.get("rating");
            validateRating(rating);
        }
        return ResponseEntity.ok(hotelService.patchHotel(id, fields));
    }

    // --- Delete ---

    @DeleteMapping("/hotels/{id}")
    @Operation(summary = "Delete a hotel chain", description = "Removes a hotel chain from the data store")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id) {
        if (hotelService.deleteHotel(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Compare ---

    @GetMapping("/hotels/compare")
    @Operation(summary = "Compare hotel chains", description = "Returns a side-by-side comparison of the specified chains")
    public List<HotelChain> compareHotels(
            @Parameter(description = "Comma-separated chain IDs (e.g. kimpton,hilton,hyatt)")
            @RequestParam List<String> ids) {
        if (ids.isEmpty() || ids.size() > 5) {
            throw new IllegalArgumentException("Provide between 1 and 5 hotel IDs to compare");
        }
        return hotelService.compareHotels(ids);
    }

    // --- Recommend ---

    @GetMapping("/hotels/recommend")
    @Operation(summary = "Get recommendations", description = "Returns hotel chains ranked by best match for your dog's weight and budget")
    public List<HotelChain> recommendHotels(
            @Parameter(description = "Your dog's weight in lbs")
            @RequestParam(required = false) Integer weight,
            @Parameter(description = "Maximum pet fee you're willing to pay")
            @RequestParam(required = false) Integer maxFee) {
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("weight must be a non-negative number");
        }
        validateMaxFee(maxFee);
        return hotelService.recommendHotels(weight, maxFee);
    }

    // --- Stats ---

    @GetMapping("/stats")
    @Operation(summary = "Get summary statistics", description = "Returns aggregate stats about all hotel chain pet policies")
    public Map<String, Object> getStats() {
        return hotelService.getStats();
    }

    // --- Validation helpers ---

    private void validateMinWeight(Integer minWeight) {
        if (minWeight != null && minWeight < 0) {
            throw new IllegalArgumentException("minWeight must be a non-negative number");
        }
    }

    private void validateMaxFee(Integer maxFee) {
        if (maxFee != null && maxFee < 0) {
            throw new IllegalArgumentException("maxFee must be a non-negative number");
        }
    }

    private void validateRating(String rating) {
        if (rating != null && !VALID_RATINGS.contains(rating.toLowerCase())) {
            throw new IllegalArgumentException("rating must be one of: excellent, good, moderate");
        }
    }

    private void validateHotelBody(HotelChain hotel) {
        if (hotel.getId() == null || hotel.getId().isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (hotel.getName() == null || hotel.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (hotel.getRating() != null) {
            validateRating(hotel.getRating());
        }
    }
}
