package ir.aut.secondhand.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class SearchAdvertisementRequest {

    public enum SortOption {
        NEWEST,
        CHEAPEST,
        EXPENSIVE
    }

    @Size(min = 3, max = 50, message = "Keyword length must be between 3 and 50 characters")
    private String keyword;

    private Long categoryId;
    private Long locationId;

    @Min(value = 0, message = "Minimum price cannot be negative")
    private Long minPrice;

    @Min(value = 0, message = "Maximum price cannot be negative")
    private Long maxPrice;

    private Map<String, Object> dynamicFilters;

    private SortOption sortBy = SortOption.NEWEST;

    @Min(value = 0, message = "Page index cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100 items")
    private int size = 10;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Map<String, Object> getDynamicFilters() {
        return dynamicFilters;
    }

    public void setDynamicFilters(Map<String, Object> dynamicFilters) {
        this.dynamicFilters = dynamicFilters;
    }

    public SortOption getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortOption sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}