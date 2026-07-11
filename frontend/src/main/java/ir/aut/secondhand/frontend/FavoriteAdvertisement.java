package ir.aut.secondhand.frontend;

public class FavoriteAdvertisement {
    private final String title;
    private final String price;
    private final String cityCategory;
    private final String description;
    private final String imagePath;

    public FavoriteAdvertisement(String title, String price, String cityCategory, String description, String imagePath){
        this.title =title;
        this.price = price;
        this.cityCategory = cityCategory;
        this.description = description;
        this.imagePath = imagePath;

    }

    public String getTitle() {return title;}
    public String getPrice() {return price;}
    public String getCityCategory() {return cityCategory;}
    public String getDescription() {return description;}
    public String getImagePath() {return imagePath;}
}
