package ir.aut.secondhand.frontend.dto;

import java.util.ArrayList;
import java.util.List;

public class LocationResponse {

    private Long id;
    private String name;
    private String type;
    private Long parentId;
    private Double latitude;
    private Double longitude;

    private List<LocationResponse> subLocations =
            new ArrayList<>();

    public LocationResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Long getParentId() {
        return parentId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List<LocationResponse> getSubLocations() {
        return subLocations;
    }

    @Override
    public String toString() {
        return name;
    }
}