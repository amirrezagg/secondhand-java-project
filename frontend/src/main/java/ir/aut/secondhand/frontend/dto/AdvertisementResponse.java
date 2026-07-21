package ir.aut.secondhand.frontend.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvertisementResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Long categoryId;
    private Long locationId;
    private String adStatus;
    private Map<String, Object> dynamicAttributes = new HashMap<>();
    private List<String> imageUrls = new ArrayList<>();
    private String sellerName;
    private String sellerUsername;
    private Long sellerId;
    private boolean favorited;
    private String categoryName;
    private String locationName;

    public AdvertisementResponse() {
    }

    public Long getId() {
        return id;
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

    public String getCategoryName() {
        return categoryName;
    }

    public String getLocationName() {
        return locationName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getAdStatus() {
        return adStatus;
    }

    public Map<String, Object> getDynamicAttributes() {
        return dynamicAttributes;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public boolean isFavorited() {
        return favorited;
    }
}
