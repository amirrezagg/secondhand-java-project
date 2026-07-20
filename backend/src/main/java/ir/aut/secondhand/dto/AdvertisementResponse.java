package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.AdminComment;
import ir.aut.secondhand.model.Advertisement;
import ir.aut.secondhand.model.Price;

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
    private Price.Currency priceCurrency;
    private Long categoryId;
    private Long locationId;
    private Advertisement.AdvertisementStatus adStatus;
    private Map<String, Object> dynamicAttributes = new HashMap<>();
    private List<AdminCommentResponse> adminComments = new ArrayList<>();
    private boolean isFavorited;

    public AdvertisementResponse(Advertisement advertisement) {
        this.id = advertisement.getId();
        this.title = advertisement.getTitle();
        this.description = advertisement.getDescription();
        this.priceAmount = advertisement.getPrice().getAmount();
        this.priceCurrency = advertisement.getPrice().getCurrency();
        this.categoryId = advertisement.getCategory().getId();
        this.locationId = advertisement.getLocation().getId();
        this.adStatus = advertisement.getStatus();
        this.dynamicAttributes = advertisement.getDynamicAttributes();
    }

    public AdvertisementResponse(Long id, String title, String description, BigDecimal priceAmount, Price.Currency priceCurrency, Long categoryId, Long locationId, Map<String, Object> dynamicAttributes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.categoryId = categoryId;
        this.locationId = locationId;
        this.dynamicAttributes = dynamicAttributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<AdminCommentResponse> getAdminComments() {
        return adminComments;
    }

    public void setAdminComments(List<AdminComment> comments) {
        if (comments != null) {
            for (AdminComment c : comments) {
                this.adminComments.add(new AdminCommentResponse(c));
            }
        }
    }

    public Advertisement.AdvertisementStatus getAdStatus() {
        return adStatus;
    }

    public void setAdStatus(Advertisement.AdvertisementStatus adStatus) {
        this.adStatus = adStatus;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}
