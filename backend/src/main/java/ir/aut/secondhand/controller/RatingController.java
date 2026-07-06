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

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<?> rateSeller(@Valid @RequestBody RateUserRequest request) {
        ratingService.rate(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/seller/{sellerId}/average")
    public ResponseEntity<AverageRateResponse> getAverageRating(@PathVariable Long sellerId) {
        Double average = ratingService.getAverageRating(sellerId);
        return ResponseEntity.ok(new AverageRateResponse(sellerId, average));
    }

}
