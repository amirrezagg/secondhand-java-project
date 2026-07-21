package ir.aut.secondhand.controller;

import ir.aut.secondhand.dto.LocationResponse;
import ir.aut.secondhand.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for location management operations.
 * Provides endpoints for retrieving location data in hierarchical tree format,
 * enabling frontend components to render location selection with parent-child
 * relationships for advertisement categorization and filtering.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Retrieves the complete hierarchical tree structure of all locations.
     * Returns locations organized as nested parent-child relationships,
     * suitable for rendering cascading dropdown menus or tree-based navigation
     * for advertisement location selection.
     *
     * @return a list of root {@link LocationResponse} objects with nested sub-locations,
     *         wrapped in HTTP 200 OK response
     */
    @GetMapping("/tree")
    public ResponseEntity<List<LocationResponse>> getAllLocationsTree() {
        return ResponseEntity.ok(locationService.getAllLocationsTree());
    }
}