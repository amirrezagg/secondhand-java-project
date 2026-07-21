package ir.aut.secondhand.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.FavoriteResponse;
import ir.aut.secondhand.service.FavoriteService;

/**
 * REST controller providing endpoints for managing the authenticated user's favorite advertisements.
 * <p>
 * Delegates toggle and retrieval operations to the service layer and maps results into consistent API
 * responses for the front-end client.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{advertisementId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long advertisementId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("isFavorited", favoriteService.toggleFavorite(advertisementId));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        List<FavoriteResponse> favorites = favoriteService.getFavorites();
        return ResponseEntity.ok(favorites);
    }
}