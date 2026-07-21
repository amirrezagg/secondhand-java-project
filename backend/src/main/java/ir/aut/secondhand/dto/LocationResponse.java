package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationResponse {
    private Long id;
    private String name;
    private String type;
    private Long parentId;
    private Double latitude;
    private Double longitude;
    private List<LocationResponse> subLocations = new ArrayList<>();

    public LocationResponse(Location location) {
        this.id = location.getId();
        this.name = location.getName();
        this.type = location.getType() != null ? location.getType().name() : null;
        this.parentId = location.getParent() != null ? location.getParent().getId() : null;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<LocationResponse> getSubLocations() {
        return subLocations;
    }

    public void setSubLocations(List<LocationResponse> subLocations) {
        this.subLocations = subLocations;
    }
}