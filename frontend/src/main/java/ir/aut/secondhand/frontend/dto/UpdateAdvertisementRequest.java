package ir.aut.secondhand.frontend.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UpdateAdvertisementRequest {

    private String title;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Long categoryId;
    private Long locationId;
    private Map<String, Object> dynamicAttributes =
            new HashMap<>();

    public UpdateAdvertisementRequest() {
    }

    public UpdateAdvertisementRequest(
            String title,
            String description,
            BigDecimal priceAmount,
            String priceCurrency,
            Long categoryId,
            Long locationId
    ) {
        this.title = title;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.categoryId = categoryId;
        this.locationId = locationId;
    }

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

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
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

    public void setDynamicAttributes(
            Map<String, Object> dynamicAttributes
    ) {
        this.dynamicAttributes =
                dynamicAttributes == null
                        ? new HashMap<>()
                        : dynamicAttributes;
    }
}
