package ir.aut.secondhand.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ir.aut.secondhand.dto.AdvertisementResponse;
import ir.aut.secondhand.dto.CreateAdvertisementRequest;
import ir.aut.secondhand.dto.ReviewAdvertisementRequest;
import ir.aut.secondhand.dto.SearchAdvertisementRequest;
import ir.aut.secondhand.dto.UpdateAdvertisementRequest;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.service.AdvertisementService;
import ir.aut.secondhand.service.ImageStorageService;
import jakarta.validation.Valid;

/**
 * Controller for advertisement lifecycle operations in the secondhand marketplace.
 * Handles REST endpoint mappings for creation, retrieval, update, deletion, review,
 * status changes, search, and image retrieval while delegating business rules to
 * the service layer.
 */
@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private static final Logger log = LoggerFactory.getLogger(AdvertisementController.class);
    private final AdvertisementService advertisementService;
    private final ImageStorageService imageStorageService;

    public AdvertisementController(AdvertisementService advertisementService, ImageStorageService imageStorageService) {
        this.advertisementService = advertisementService;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Creates a new advertisement using validated client payload.
     * Returns a representation of the persisted advertisement.
     */
    @PostMapping("/")
    public ResponseEntity<?> createAdvertisement(@Valid @RequestBody CreateAdvertisementRequest advertisementRequest){
        Advertisement advertisement = advertisementService.createAdvertisement(advertisementRequest);
        return ResponseEntity.ok(new AdvertisementResponse(advertisement));
    }

    /**
     * Returns advertisements submitted by the authenticated user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<AdvertisementResponse>> getMyAdvertisements(){
        return ResponseEntity.ok(advertisementService.getMyAdvertisements());
    }

    /**
     * Returns advertisements visible to the current user according to their role.
     */
    @GetMapping
    public ResponseEntity<List<AdvertisementResponse>> getAdvertisements(){
        return ResponseEntity.ok(advertisementService.getAdvertisementsByRole());
    }

    /**
     * Returns advertisements that are awaiting administrative approval.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdvertisementResponse>> getPendingAdvertisements(){
        return ResponseEntity.ok(advertisementService.getPendingAdvertisements());
    }

    /**
     * Returns full details for the requested advertisement.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> getAdvertisementDetails(@PathVariable Long id){
        return ResponseEntity.ok(advertisementService.getAdvertisementDetails(id));
    }

    /**
     * Uploads one or more images for a specific advertisement and assigns the main image index.
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, Object>> uploadAdvertisementImages(
            @PathVariable Long id,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "mainImageIndex", defaultValue = "0") int mainImageIndex) {

        // Delegate image persistence and main-image assignment logic to the service layer.
        advertisementService.saveAdvertisementImages(id, files, mainImageIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Updates advertisement metadata based on the provided request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody UpdateAdvertisementRequest request) {

        Advertisement updatedAd = advertisementService.updateAdvertisement(id, request);
        return ResponseEntity.ok(new AdvertisementResponse(updatedAd));
    }

    /**
     * Deletes the specified advertisement and returns a confirmation payload.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisement(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Processes an administrative review action for the advertisement.
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<AdvertisementResponse> reviewAdvertisement(
            @PathVariable Long id,
            @RequestBody ReviewAdvertisementRequest request) {

        AdvertisementResponse response = advertisementService.reviewAdvertisement(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Marks an advertisement as sold and returns the updated representation.
     */
    @PutMapping("/{id}/sold")
    public ResponseEntity<AdvertisementResponse> markAsSold(@PathVariable Long id) {
        AdvertisementResponse response = advertisementService.markAsSold(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches advertisements using the supplied criteria and returns matching results.
     */
    @PostMapping("/search")
    public ResponseEntity<List<AdvertisementResponse>> searchAdvertisements(
            @Valid @RequestBody SearchAdvertisementRequest request) {

        List<AdvertisementResponse> results = advertisementService.searchAdvertisements(request);
        return ResponseEntity.ok(results);
    }

    private final Path rootLocation = Path.of("uploads", "advertisements");

    /**
     * Serves stored advertisement images from the local upload directory.
     * Attempts to resolve the requested filename and returns the image resource
     * with an appropriate MIME type or a not-found / server-error status.
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            log.info(rootLocation.toAbsolutePath().toString());
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Infer basic supported image MIME types for the response.
                String contentType = "image/jpeg";
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
