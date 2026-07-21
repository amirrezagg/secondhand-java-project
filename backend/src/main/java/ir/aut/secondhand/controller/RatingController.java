package ir.aut.secondhand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.AverageRateResponse;
import ir.aut.secondhand.dto.RateUserRequest;
import ir.aut.secondhand.service.RatingService;
import jakarta.validation.Valid;

/**
 * REST controller exposing endpoints for seller rating operations.
 *
 * Handles incoming rating submissions and average rating queries,
 * delegating core business processing to the RatingService.
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Accepts a validated rating request and forwards it to the service layer.
     *
     * Returns a simple success payload after the rating has been processed.
     */
    @PostMapping
    public ResponseEntity<?> rateSeller(@Valid @RequestBody RateUserRequest request) {
        // Delegate request handling to rating service for persistence and business rules.
        ratingService.rate(request);

        return ResponseEntity.ok(java.util.Map.of("message", "success"));
    }

    /**
     * Retrieves the average rating for the specified seller.
     *
     * The response is wrapped in an AverageRateResponse DTO for API consistency.
     */
    @GetMapping("/seller/{sellerId}/average")
    public ResponseEntity<AverageRateResponse> getAverageRating(@PathVariable Long sellerId) {
        // Query service layer for computed average rating value.
        Double average = ratingService.getAverageRating(sellerId);
        return ResponseEntity.ok(new AverageRateResponse(sellerId, average));
    }

}
