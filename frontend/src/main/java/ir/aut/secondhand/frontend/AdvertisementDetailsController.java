package ir.aut.secondhand.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class AdvertisementDetailsController {

    @FXML
    private ImageView mainImageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label cityCategoryLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label sellerLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private String previousPage = "home";

    @FXML
    private String currentTitle;

    @FXML
    private String currentPrice;

    @FXML
    private String currentCityCategory;

    @FXML
    private String currentDescription;

    @FXML
    private String currentImagePath;

    @FXML
    private Label favoriteMessageLabel;

    @FXML
    private Button addToFavoritesButton;

    @FXML
    private void goBack() {
        try {

            String fxmlPath;

            if (previousPage.equals("favorites")){
                fxmlPath = "/ir/aut/secondhand/frontend/fxml/favorites-view.fxml";
            }
            else{
                fxmlPath = "/ir/aut/secondhand/frontend/fxml/home-view.fxml";
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) mainImageView.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(maximized);
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void setAdvertisementDetails(String title, String price, String cityCategory, String description, String imagePath){

        currentTitle = title;
        currentPrice = price;
        currentCityCategory = cityCategory;
        currentDescription = description;
        currentImagePath = imagePath;

        titleLabel.setText(title);
        priceLabel.setText(price);
        cityCategoryLabel.setText(cityCategory);
        descriptionLabel.setText(description);

        mainImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    @FXML
    private void addToFavorites() {

        FavoriteAdvertisement advertisement = new FavoriteAdvertisement(currentTitle, currentPrice, currentCityCategory, currentDescription, currentImagePath);

        boolean alreadyExists = FavoritesManager.getFavorites()
                .stream()
                .anyMatch(favorite ->
                        favorite.getTitle().equals(currentTitle)
                );

        if (alreadyExists) {
            favoriteMessageLabel.setStyle(
                    "-fx-text-fill: #d97706; -fx-font-weight: bold;"
            );
            favoriteMessageLabel.setText(
                    "این آگهی قبلاً به علاقه‌مندی‌ها اضافه شده است."
            );
            return;
        }

        FavoritesManager.addFavorite(advertisement);

        favoriteMessageLabel.setStyle(
                "-fx-text-fill: green; -fx-font-weight: bold;"
        );
        favoriteMessageLabel.setText(
                "آگهی با موفقیت به علاقه‌مندی‌ها اضافه شد."
        );
    }

    @FXML
    private void messageSeller() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/messages-view.fxml"));

            Parent root = fxmlLoader.load();

            MessagesController controller = fxmlLoader.getController();
            controller.openConversation("Amirreza");

            Stage stage = (Stage) mainImageView.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

            stage.setScene(scene);

            if (maximized){
                stage.setMaximized(false);
                Platform.runLater(() -> stage.setMaximized(true));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setPreviousPage(String previousPage){
        this.previousPage = previousPage;

        if (previousPage.equals("favorites")){
            addToFavoritesButton.setVisible(false);
            addToFavoritesButton.setManaged(false);
        }
    }
}
