package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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
            String cityCategory,
            String description,
            String imagePath
    ) {

        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER_RIGHT);
        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #e5e7eb;"
                        + "-fx-border-radius: 12;"
                        + "-fx-padding: 15;"
                        + "-fx-cursor: hand;"
        );

        ImageView imageView = new ImageView();

        Image image = loadImage(imagePath);

        if (image != null) {
            imageView.setImage(image);
        }

        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        Label priceLabel = new Label(price);

        priceLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #1E88E5;"
                        + "-fx-font-weight: bold;"
        );

        Label locationLabel = new Label(cityCategory);

        locationLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #6b7280;"
        );

        Button removeButton =
                new Button("Remove from favorites");

        removeButton.setOnAction(event -> {

            /*
             * مانع می‌شود کلیک روی دکمه Remove،
             * صفحه جزئیات آگهی را هم باز کند.
             */
            event.consume();

            FavoritesManager.removeFavorite(title);
            loadFavorites();
        });

        card.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                locationLabel,
                removeButton
        );

        card.setOnMouseClicked(event ->
                openAdvertisementDetails(
                        title,
                        price,
                        cityCategory,
                        description,
                        "Unknown seller",
                        imagePath
                )
        );

        return card;
    }

    private Image loadImage(String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {

            if (imagePath.startsWith("http://")
                    || imagePath.startsWith("https://")) {

                return new Image(
                        imagePath,
                        true
                );
            }

            URL resource =
                    getClass().getResource(imagePath);

            if (resource == null) {
                return null;
            }

            return new Image(
                    resource.toExternalForm()
            );

        } catch (Exception exception) {

            exception.printStackTrace();
            return null;
        }
    }

    @FXML
    private void goBack() {

        try {

            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml"
                            )
                    );

            Parent root = fxmlLoader.load();

            Stage stage =
                    (Stage) favoritesTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene =
                    new Scene(
                            root,
                            width,
                            height
                    );

            URL stylesheet =
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/css/style.css"
                    );

            if (stylesheet != null) {
                scene.getStylesheets().add(
                        stylesheet.toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void openAdvertisementDetails(
            String title,
            String price,
            String cityCategory,
            String description,
            String sellerName,
            String imagePath
    ) {

        try {

            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/advertisement-details-view.fxml"
                            )
                    );

            Parent root = fxmlLoader.load();

            AdvertisementDetailsController controller =
                    fxmlLoader.getController();

            controller.setPreviousPage("favorites");

            controller.setAdvertisementDetails(
                    title,
                    price,
                    cityCategory,
                    description,
                    sellerName,
                    imagePath
            );

            Stage stage =
                    (Stage) favoritesTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene =
                    new Scene(
                            root,
                            width,
                            height
                    );

            URL stylesheet =
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/css/style.css"
                    );

            if (stylesheet != null) {
                scene.getStylesheets().add(
                        stylesheet.toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void loadFavorites() {

        favoritesTilePane
                .getChildren()
                .clear();

        for (FavoriteAdvertisement advertisement
                : FavoritesManager.getFavorites()) {

            favoritesTilePane
                    .getChildren()
                    .add(
                            createFavoriteCard(
                                    advertisement.getTitle(),
                                    advertisement.getPrice(),
                                    advertisement.getCityCategory(),
                                    advertisement.getDescription(),
                                    advertisement.getImagePath()
                            )
                    );
        }
    }
}