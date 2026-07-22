package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import ir.aut.secondhand.frontend.dto.FavoritesResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FavoritesController {

    private static final String SERVER_URL =
            "http://localhost:8080";

    private static final String DEFAULT_IMAGE =
            "/ir/aut/secondhand/frontend/images/laptop.png";

    private final ApiClient apiClient =
            new ApiClient();

    @FXML
    private TilePane favoritesTilePane;

    @FXML
    public void initialize() {
        loadFavorites();
    }

    private void loadFavorites() {

        favoritesTilePane
                .getChildren()
                .clear();

        Label loadingLabel =
                new Label("Loading favorites...");

        loadingLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #6b7280;"
        );

        favoritesTilePane
                .getChildren()
                .add(loadingLabel);

        Task<List<FavoritesResponse>> task =
                new Task<>() {

                    @Override
                    protected List<FavoritesResponse> call()
                            throws Exception {

                        return apiClient.getFavorites();
                    }
                };

        task.setOnSucceeded(event -> {

            favoritesTilePane
                    .getChildren()
                    .clear();

            List<FavoritesResponse> favorites =
                    task.getValue();

            if (favorites == null
                    || favorites.isEmpty()) {

                showEmptyFavorites();
                return;
            }

            for (FavoritesResponse favorite : favorites) {

                if (favorite == null
                        || favorite.getAdvertisement() == null) {

                    continue;
                }

                AdvertisementResponse advertisement =
                        favorite.getAdvertisement();

                favoritesTilePane
                        .getChildren()
                        .add(
                                createFavoriteCard(
                                        advertisement
                                )
                        );
            }

            if (favoritesTilePane
                    .getChildren()
                    .isEmpty()) {

                showEmptyFavorites();
            }
        });

        task.setOnFailed(event -> {

            task.getException()
                    .printStackTrace();

            favoritesTilePane
                    .getChildren()
                    .clear();

            Label errorLabel =
                    new Label(
                            "Could not load favorites."
                    );

            errorLabel.setStyle(
                    "-fx-font-size: 14px;"
                            + "-fx-text-fill: #dc2626;"
                            + "-fx-font-weight: bold;"
            );

            favoritesTilePane
                    .getChildren()
                    .add(errorLabel);
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    private VBox createFavoriteCard(
            AdvertisementResponse advertisement
    ) {

        VBox card =
                new VBox(8);

        card.setAlignment(
                Pos.CENTER_RIGHT
        );

        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #e5e7eb;"
                        + "-fx-border-radius: 12;"
                        + "-fx-padding: 15;"
                        + "-fx-cursor: hand;"
        );

        List<String> imageUrls =
                normalizeImageUrls(
                        advertisement.getImageUrls()
                );

        String mainImagePath =
                imageUrls.isEmpty()
                        ? DEFAULT_IMAGE
                        : imageUrls.get(0);

        ImageView imageView =
                new ImageView();

        Image image =
                loadImage(mainImagePath);

        if (image != null) {
            imageView.setImage(image);
        }

        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        String title =
                advertisement.getTitle() == null
                        ? "Untitled advertisement"
                        : advertisement.getTitle();

        Label titleLabel =
                new Label(title);

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        String formattedPrice =
                advertisement.getPriceAmount() == null
                        ? "Price not specified"
                        : String.format(
                        "%,d تومان",
                        advertisement
                                .getPriceAmount()
                                .longValue()
                );

        Label priceLabel =
                new Label(formattedPrice);

        priceLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #1E88E5;"
                        + "-fx-font-weight: bold;"
        );

        String locationName =
                advertisement.getLocationName() == null
                        || advertisement
                        .getLocationName()
                        .isBlank()
                        ? "Unknown location"
                        : advertisement.getLocationName();

        String categoryName =
                advertisement.getCategoryName() == null
                        || advertisement
                        .getCategoryName()
                        .isBlank()
                        ? "Unknown category"
                        : advertisement.getCategoryName();

        String cityCategory =
                locationName
                        + " • "
                        + categoryName;

        Label locationLabel =
                new Label(cityCategory);

        locationLabel.setWrapText(true);

        locationLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #6b7280;"
        );

        Button removeButton =
                new Button(
                        "Remove from favorites"
                );

        removeButton.setMaxWidth(
                Double.MAX_VALUE
        );

        removeButton.setOnAction(event -> {

            event.consume();

            removeFromFavorites(
                    advertisement.getId(),
                    removeButton
            );
        });

        card.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                locationLabel,
                removeButton
        );

        card.setOnMouseClicked(event -> {

            if (event.isStillSincePress()) {

                openAdvertisementDetails(
                        advertisement,
                        formattedPrice,
                        cityCategory,
                        mainImagePath,
                        imageUrls
                );
            }
        });

        return card;
    }

    private void removeFromFavorites(
            Long advertisementId,
            Button removeButton
    ) {

        if (advertisementId == null) {

            showError(
                    "Advertisement ID is not available."
            );

            return;
        }

        removeButton.setDisable(true);
        removeButton.setText("Removing...");

        Task<Void> task =
                new Task<>() {

                    @Override
                    protected Void call()
                            throws Exception {

                        apiClient.toggleFavorite(
                                advertisementId
                        );

                        return null;
                    }
                };

        task.setOnSucceeded(event ->
                loadFavorites()
        );

        task.setOnFailed(event -> {

            task.getException()
                    .printStackTrace();

            removeButton.setDisable(false);

            removeButton.setText(
                    "Remove from favorites"
            );

            showError(
                    "Could not remove this advertisement from favorites."
            );
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    private List<String> normalizeImageUrls(
            List<String> storedImageUrls
    ) {

        List<String> normalizedUrls =
                new ArrayList<>();

        if (storedImageUrls == null) {
            return normalizedUrls;
        }

        for (String storedUrl : storedImageUrls) {

            if (storedUrl == null
                    || storedUrl.isBlank()) {

                continue;
            }

            if (storedUrl.startsWith("http://")
                    || storedUrl.startsWith("https://")) {

                normalizedUrls.add(storedUrl);

            } else if (storedUrl.startsWith("/")) {

                normalizedUrls.add(
                        SERVER_URL + storedUrl
                );

            } else {

                normalizedUrls.add(storedUrl);
            }
        }

        return normalizedUrls;
    }

    private Image loadImage(
            String imagePath
    ) {

        if (imagePath == null
                || imagePath.isBlank()) {

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
                    getClass().getResource(
                            imagePath
                    );

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

    private void openAdvertisementDetails(
            AdvertisementResponse advertisement,
            String formattedPrice,
            String cityCategory,
            String mainImagePath,
            List<String> imageUrls
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/advertisement-details-view.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            AdvertisementDetailsController controller =
                    loader.getController();

            controller.setPreviousPage(
                    "favorites"
            );

            String description =
                    advertisement.getDescription() == null
                            ? ""
                            : advertisement.getDescription();

            String sellerName =
                    advertisement.getSellerName() == null|| advertisement
                            .getSellerName()
                            .isBlank()
                            ? "Unknown seller"
                            : advertisement.getSellerName();

            controller.setAdvertisementDetails(
                    advertisement.getTitle(),
                    formattedPrice,
                    cityCategory,
                    description,
                    sellerName,
                    mainImagePath,
                    imageUrls
            );

            Stage stage =
                    (Stage) favoritesTilePane
                            .getScene()
                            .getWindow();

            double width =
                    stage.getWidth();

            double height =
                    stage.getHeight();

            boolean maximized =
                    stage.isMaximized();

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

                scene.getStylesheets()
                        .add(
                                stylesheet.toExternalForm()
                        );
            }

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {

            exception.printStackTrace();

            showError(
                    "Could not open advertisement details."
            );
        }
    }

    private void showEmptyFavorites() {

        Label emptyLabel =
                new Label(
                        "You have no favorite advertisements."
                );

        emptyLabel.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-text-fill: #6b7280;"
        );

        favoritesTilePane
                .getChildren()
                .add(emptyLabel);
    }

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Favorites Error"
        );

        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/home-view.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage) favoritesTilePane
                            .getScene()
                            .getWindow();

            double width =
                    stage.getWidth();

            double height =
                    stage.getHeight();

            boolean maximized =
                    stage.isMaximized();

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

                scene.getStylesheets()
                        .add(
                                stylesheet.toExternalForm()
                        );
            }

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {

            exception.printStackTrace();

            showError(
                    "Could not return to Home."
            );
        }
    }
}