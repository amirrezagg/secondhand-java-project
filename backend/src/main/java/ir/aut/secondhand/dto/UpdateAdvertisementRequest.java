package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Price;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UpdateAdvertisementRequest {

    @Size(max = 70, message = "Title length  must not exceed 70.")
    @Size(min = 3, message = "Title length must not be less than 3")
    private String title;

    @Size(max = 1000, message = "Description length must not exceed 1000")
    @Size(min = 10, message = "Description length must not be less than 10")
    private String description;

    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal priceAmount;

    private Price.Currency priceCurrency;

    private Long categoryId;

    private Long locationId;

    private Map<String, Object> dynamicAttributes = new HashMap<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public Price.Currency getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(Price.Currency priceCurrency) {
        this.priceCurrency = priceCurrency;
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

    public Map<String, Object> getDynamicAttributes() {
        return dynamicAttributes;
    }

    public void setDynamicAttributes(Map<String, Object> dynamicAttributes) {
        this.dynamicAttributes = dynamicAttributes;
    }
}