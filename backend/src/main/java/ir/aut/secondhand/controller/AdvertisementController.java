package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.*;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.service.AdvertisementService;
import ir.aut.secondhand.service.ImageStorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final ImageStorageService imageStorageService;

    public AdvertisementController(AdvertisementService advertisementService, ImageStorageService imageStorageService) {
        this.advertisementService = advertisementService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createAdvertisement(@Valid @RequestBody CreateAdvertisementRequest advertisementRequest){
        Advertisement advertisement = advertisementService.createAdvertisement(advertisementRequest);
        return ResponseEntity.ok(new AdvertisementResponse(advertisement));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AdvertisementResponse>> getMyAdvertisements(){
        return ResponseEntity.ok(advertisementService.getMyAdvertisements());
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAdvertisements(){
        return ResponseEntity.ok(advertisementService.getAdvertisementsByRole());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementResponse>> getPendingAdvertisements(){
        return ResponseEntity.ok(advertisementService.getPendingAdvertisements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> getAdvertisementDetails(@PathVariable Long id){
        return ResponseEntity.ok(advertisementService.getAdvertisementDetails(id));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<String> uploadAdvertisementImages(
            @PathVariable Long id,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "mainImageIndex", defaultValue = "0") int mainImageIndex) {

        advertisementService.saveAdvertisementImages(id, files, mainImageIndex);

        return ResponseEntity.ok("Images uploaded successfully!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody UpdateAdvertisementRequest request) {

        Advertisement updatedAd = advertisementService.updateAdvertisement(id, request);
        return ResponseEntity.ok(new AdvertisementResponse(updatedAd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Advertisement deleted successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<AdvertisementResponse> reviewAdvertisement(
            @PathVariable Long id,
            @RequestBody ReviewAdvertisementRequest request) {

        AdvertisementResponse response = advertisementService.reviewAdvertisement(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/sold")
    public ResponseEntity<AdvertisementResponse> markAsSold(@PathVariable Long id) {
        AdvertisementResponse response = advertisementService.markAsSold(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<AdvertisementResponse>> searchAdvertisements(
            @Valid @RequestBody SearchAdvertisementRequest request) {

        List<AdvertisementResponse> results = advertisementService.searchAdvertisements(request);
        return ResponseEntity.ok(results);
    }
}
