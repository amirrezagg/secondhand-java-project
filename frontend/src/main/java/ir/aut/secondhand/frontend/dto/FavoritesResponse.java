package ir.aut.secondhand.frontend.dto;

public class FavoritesResponse {

    private Long id;
    private String createdAt;
    private AdvertisementResponse advertisement;

    public FavoritesResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public AdvertisementResponse getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(
            AdvertisementResponse advertisement
    ) {
        this.advertisement = advertisement;
    }
}