package ir.aut.secondhand.frontend.dto;

import java.util.Map;

public class SearchAdvertisementRequest {

    private String keyword;
    private Long categoryId;
    private Long locationId;
    private Long minPrice;
    private Long maxPrice;
    private Map<String, Object> dynamicFilters;
    private String sortBy = "NEWEST";
    private int page = 0;
    private int size = 100;

    public SearchAdvertisementRequest() {
    }

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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
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