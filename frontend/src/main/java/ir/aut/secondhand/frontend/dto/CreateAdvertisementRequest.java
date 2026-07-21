package ir.aut.secondhand.frontend.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CreateAdvertisementRequest {

    private String title;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Long categoryId;
    private Long locationId;
    private Map<String, Object> dynamicAttributes;

    public CreateAdvertisementRequest(
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
        this.dynamicAttributes = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Map<String, Object> getDynamicAttributes() {
        return dynamicAttributes;
    }
}
