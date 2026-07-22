package ir.aut.secondhand.frontend;

import java.util.List;
import java.util.ArrayList;

public class Advertisement {

    private long id;
    private String title;
    private String description;
    private long price;
    private String city;
    private String category;
    private String imagePath;
    private List<String> imageUrls = new ArrayList<>();
    private String status;
    private String sellerName;
    private Long sellerId;

    public Advertisement(
            long id,
            String title,
            String description,
            long price,
            String city,
            String category,
            String imagePath,
            String status,
            String sellerName,
            List<String> imageUrls,
            Long sellerId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.city = city;
        this.category = category;
        this.imagePath = imagePath;
        this.status = status;
        this.sellerName = sellerName;
        this.sellerId = sellerId;

        if (imageUrls != null){
            this.imageUrls = new ArrayList<>(imageUrls);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getSellerName() {
        return sellerName;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
