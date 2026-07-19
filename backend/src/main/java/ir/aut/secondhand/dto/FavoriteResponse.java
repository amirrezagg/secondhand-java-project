package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.Favorite;

import java.time.LocalDateTime;

public class FavoriteResponse {

    private Long id;
    private LocalDateTime createdAt;
    private AdvertisementResponse advertisement;

    public FavoriteResponse(Favorite favorite) {
        this.id = favorite.getId();
        this.createdAt = favorite.getCreatedAt();
        if (favorite.getAdvertisement() != null) {
            this.advertisement = new AdvertisementResponse(favorite.getAdvertisement());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AdvertisementResponse getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(AdvertisementResponse advertisement) {
        this.advertisement = advertisement;
    }
}