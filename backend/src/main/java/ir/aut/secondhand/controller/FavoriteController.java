package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.FavoriteResponse;
import ir.aut.secondhand.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{advertisementId}/toggle")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long advertisementId) {
        String message = favoriteService.toggleFavorite(advertisementId);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        List<FavoriteResponse> favorites = favoriteService.getFavorites();
        return ResponseEntity.ok(favorites);
    }
}