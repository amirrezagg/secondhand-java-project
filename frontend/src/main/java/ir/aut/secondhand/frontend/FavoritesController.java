package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class FavoritesController {

    @FXML
    private TilePane favoritesTilePane;

    @FXML
    public void initialize() {
        loadFavorites();
    }

    private VBox createFavoriteCard(
            String title,
            String price,
            String city,
            String category,
            String imagePath
    ) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER_RIGHT);
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 15;"
        );

        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1E88E5; -fx-font-weight: bold;");

        Label locationLabel = new Label(city + " • " + category);
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        Button removeButton = new Button("Remove from favoritef");

        removeButton.setOnAction(event -> {
            FavoritesManager.removeFavorite(title);
            loadFavorites();
        });

        card.getChildren().addAll(imageView, titleLabel, priceLabel, locationLabel, removeButton);

        card.setOnMouseClicked(event -> openAdvertisementDetails(
                title,
                price,
                city + " • " + category,
                "این آگهی نمونه برای نمایش صفحه جزئیات آگهی است.",
                imagePath
        ));

        return card;
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) favoritesTilePane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAdvertisementDetails(String title, String price, String cityCategory, String description, String imagePath) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/advertisement-details-view.fxml"));

            Parent root = fxmlLoader.load();

            AdvertisementDetailsController controller = fxmlLoader.getController();
            controller.setPreviousPage("favorites");
            controller.setAdvertisementDetails(title, price, cityCategory, description, imagePath);

            Stage stage = (Stage) favoritesTilePane.getScene().getWindow();

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

    private void loadFavorites(){
        favoritesTilePane.getChildren().clear();

        for (FavoriteAdvertisement advertisement : FavoritesManager.getFavorites()){
            favoritesTilePane.getChildren().add(createFavoriteCard(advertisement.getTitle(), advertisement.getPrice(), advertisement.getCityCategory(), advertisement.getDescription(), advertisement.getImagePath()));
        }
    }
}
