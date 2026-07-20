package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.FavoriteResponse;
import ir.aut.secondhand.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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