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
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.HBox;
import ir.aut.secondhand.frontend.api.ApiClient;
import javafx.concurrent.Task;

public class AdvertisementDetailsController {

    private final ApiClient apiClient = new ApiClient();

    private Long advertisementId;

    @FXML
    private ImageView mainImageView;

    @FXML
    private HBox thumbnailBox;

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
    private Button rateSellerButton;

    @FXML
    private Label sellerRatingStarsLabel;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private Label ratingCountLabel;

    private final List<Integer> sellerRatings = new ArrayList<>(List.of(4,5,4));

    @FXML
    public void initialize() {
        updateSellerRatingDisplay();
    }

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


    public void setAdvertisementDetails(
            String title,
            String price,
            String cityCategory,
            String description,
            String sellerName,
            String imagePath,
            List<String> imageUrls
    ) {
        this.advertisementId = advertisementId;

        currentTitle = title;
        currentPrice = price;
        currentCityCategory = cityCategory;
        currentDescription = description;
        currentImagePath = imagePath;

        titleLabel.setText(title);
        priceLabel.setText(price);
        cityCategoryLabel.setText(cityCategory);
        descriptionLabel.setText(description);

        sellerLabel.setText(
                sellerName == null || sellerName.isBlank()
                        ? "Unknown seller"
                        : sellerName
        );

        List<String> galleryImages =
                imageUrls == null
                        ? new ArrayList<>()
                        : new ArrayList<>(imageUrls);

        if (galleryImages.isEmpty()
                && imagePath != null
                && !imagePath.isBlank()) {

            galleryImages.add(imagePath);
        }

        thumbnailBox.getChildren().clear();

        if (galleryImages.isEmpty()) {

            mainImageView.setImage(null);

            thumbnailBox.setVisible(false);
            thumbnailBox.setManaged(false);

            return;
        }

        thumbnailBox.setVisible(
                galleryImages.size() > 1
        );

        thumbnailBox.setManaged(
                galleryImages.size() > 1
        );

        showMainImage(
                galleryImages.get(0)
        );

        for (String galleryImagePath : galleryImages) {

            Image thumbnailImage =
                    loadImage(galleryImagePath);

            if (thumbnailImage == null) {
                continue;
            }

            ImageView thumbnail =
                    new ImageView(thumbnailImage);

            thumbnail.setFitWidth(90);
            thumbnail.setFitHeight(70);
            thumbnail.setPreserveRatio(false);
            thumbnail.setSmooth(true);

            thumbnail.setStyle(
                    "-fx-cursor: hand;"
                            + "-fx-border-color: #cbd5e1;"
                            + "-fx-border-width: 2;"
                            + "-fx-border-radius: 6;"
            );

            thumbnail.setOnMouseClicked(event ->
                    showMainImage(galleryImagePath)
            );

            thumbnailBox.getChildren().add(
                    thumbnail
            );
        }
    }

    public void setAdvertisementDetails(
            Long advertisementId,
            String title,
            String price,
            String cityCategory,
            String description,
            String sellerName,
            String imagePath,
            List<String> imageUrls
    ) {

        this.advertisementId = advertisementId;

        setAdvertisementDetails(
                title,
                price,
                cityCategory,
                description,
                sellerName,
                imagePath,
                imageUrls
        );
    }

    private void showMainImage(String imagePath) {

        Image image = loadImage(imagePath);

        mainImageView.setImage(image);
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

            var resource =
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
    private void addToFavorites() {

        if (advertisementId == null) {

            favoriteMessageLabel.setStyle(
                    "-fx-text-fill: red;"
                            + "-fx-font-weight: bold;"
            );

            favoriteMessageLabel.setText(
                    "Advertisement ID not found."
            );

            return;
        }

        addToFavoritesButton.setDisable(true);

        Task<Boolean> task = new Task<>() {

            @Override
            protected Boolean call() throws Exception {

                boolean alreadyFavorite =
                        apiClient.getFavorites()
                                .stream()
                                .anyMatch(favorite ->
                                        favorite.getAdvertisement() != null
                                                && advertisementId.equals(
                                                favorite
                                                        .getAdvertisement()
                                                        .getId()
                                        )
                                );

                if (alreadyFavorite) {
                    return false;
                }

                apiClient.toggleFavorite(
                        advertisementId
                );

                return true;
            }
        };

        task.setOnSucceeded(event -> {

            addToFavoritesButton.setDisable(false);

            if (Boolean.TRUE.equals(task.getValue())) {

                favoriteMessageLabel.setStyle(
                        "-fx-text-fill: green;"
                                + "-fx-font-weight: bold;"
                );

                favoriteMessageLabel.setText(
                        "Advertisement added to favorites successfully."
                );

                addToFavoritesButton.setDisable(true);

            } else {

                favoriteMessageLabel.setStyle(
                        "-fx-text-fill: #d97706;"
                                + "-fx-font-weight: bold;"
                );

                favoriteMessageLabel.setText(
                        "This advertisement is already in your favorites."
                );
            }
        });

        task.setOnFailed(event -> {

            addToFavoritesButton.setDisable(false);

            favoriteMessageLabel.setStyle(
                    "-fx-text-fill: red;"
                            + "-fx-font-weight: bold;"
            );

            favoriteMessageLabel.setText(
                    "Could not update favorites."
            );

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

    @FXML
    public void openRatingPage() throws IOException {

        Stage stage = (Stage)
                rateSellerButton.getScene().getWindow();

        Scene previousScene = stage.getScene();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/ir/aut/secondhand/frontend/fxml/rating-view.fxml"
                )
        );

        Parent root = loader.load();

        RatingController ratingController =
                loader.getController();

        ratingController.setPreviousScene(previousScene);

        ratingController.setRatingSubmittedListener(
                rating -> {
                    sellerRatings.add(rating);
                    updateSellerRatingDisplay();
                }
        );

        Scene ratingScene = new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );

        ratingScene.getStylesheets().add(
                getClass().getResource(
                        "/ir/aut/secondhand/frontend/css/style.css"
                ).toExternalForm()
        );

        boolean maximized = stage.isMaximized();

        stage.setScene(ratingScene);
        stage.setMaximized(maximized);
    }

    private void updateSellerRatingDisplay() {

        int ratingCount = sellerRatings.size();

        if (ratingCount == 0) {

            sellerRatingStarsLabel.setText("☆☆☆☆☆");
            averageRatingLabel.setText("0.0");
            ratingCountLabel.setText("(0 ratings)");

            return;
        }

        double average = sellerRatings
                .stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        int roundedAverage =
                (int) Math.round(average);

        String stars =
                "★".repeat(roundedAverage)
                        + "☆".repeat(5 - roundedAverage);

        sellerRatingStarsLabel.setText(stars);

        averageRatingLabel.setText(
                String.format("%.1f", average)
        );

        ratingCountLabel.setText(
                "(" + ratingCount + " ratings)"
        );
    }

}
