package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.LocationResponse;
import ir.aut.secondhand.model.Location;
import ir.aut.secondhand.repository.LocationRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for location management in the second-hand marketplace.
 * Handles hierarchical location data operations, including building tree structures
 * of parent-child location relationships for use in advertisement location selection
 * and hierarchical navigation.
 */
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Retrieves the complete hierarchical tree structure of all locations.
     * Builds a parent-child relationship tree starting from root locations
     * with infinite depth, representing geographical or administrative divisions.
     *
     * @return a list of {@link LocationResponse} objects representing the root locations
     *         with their nested sub-locations recursively populated
     */
    public List<LocationResponse> getAllLocationsTree() {
        List<Location> allLocations = locationRepository.findAll();
        Map<Long, List<Location>> childrenMap = new HashMap<>();
        List<Location> roots = new ArrayList<>();

        // Build a map of parent ID to list of children for efficient tree construction
        for (Location loc : allLocations) {
            if (loc.getParent() != null) {
                Long parentId = loc.getParent().getId();
                if (!childrenMap.containsKey(parentId)) {
                    childrenMap.put(parentId, new ArrayList<>());
                }
                childrenMap.get(parentId).add(loc);
            } else {
                roots.add(loc);
            }
        }

        // Recursively build the tree structure starting from each root
        List<LocationResponse> result = new ArrayList<>();
        for (Location root : roots) {
            result.add(buildTree(root, childrenMap));
        }
        return result;
    }

    /**
     * Recursively builds a location tree node and its descendants.
     *
     * @param location the current location node to process
     * @param childrenMap map of parent ID to list of child locations
     * @return a populated {@link LocationResponse} containing the location
     *         and its nested sub-locations
     */
    private LocationResponse buildTree(Location location, Map<Long, List<Location>> childrenMap) {
        LocationResponse response = new LocationResponse(location);
        List<Location> children = childrenMap.get(location.getId());
        List<LocationResponse> childDtos = new ArrayList<>();

        // Recursively process all children of the current location
        if (children != null) {
            for (Location child : children) {
                childDtos.add(buildTree(child, childrenMap));
            }
        }

        response.setSubLocations(childDtos);
        return response;
    }
}
