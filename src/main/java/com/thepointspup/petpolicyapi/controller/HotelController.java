package com.thepointspup.petpolicyapi.controller;

import com.thepointspup.petpolicyapi.model.HotelChain;
import com.thepointspup.petpolicyapi.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Hotel Pet Policies", description = "Pet policy data for major hotel chains")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

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

    @GetMapping("/stats")
    @Operation(summary = "Get summary statistics", description = "Returns aggregate stats about all hotel chain pet policies")
    public Map<String, Object> getStats() {
        return hotelService.getStats();
    }
}
