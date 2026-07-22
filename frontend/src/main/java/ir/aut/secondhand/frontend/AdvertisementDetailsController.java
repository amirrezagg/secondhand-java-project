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
import ir.aut.secondhand.frontend.dto.AverageRateResponse;

public class AdvertisementDetailsController {

    private final ApiClient apiClient = new ApiClient();

    private Long advertisementId;

    private Long sellerId;

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
    private Button messageSellerButton;

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


    @FXML
    public void initialize() {
        sellerRatingStarsLabel.setText("☆☆☆☆☆");
        averageRatingLabel.setText("0.0");

        ratingCountLabel.setVisible(false);
        ratingCountLabel.setManaged(false);
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
        this.sellerId = sellerId;

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

        String currentUserFullName = SessionManager.getFullName();

        boolean ownAdvertisement =
                currentUserFullName != null
                        && sellerName != null
                        && currentUserFullName.equals(sellerName);

        messageSellerButton.setVisible(!ownAdvertisement);
        messageSellerButton.setManaged(!ownAdvertisement);

        rateSellerButton.setVisible(!ownAdvertisement);
        rateSellerButton.setManaged(!ownAdvertisement);

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
            Long sellerId,
            String title,
            String price,
            String cityCategory,
            String description,
            String sellerName,
            String imagePath,
            List<String> imageUrls
    ) {

        this.advertisementId = advertisementId;
        this.sellerId = sellerId;

        setAdvertisementDetails(
                title,
                price,
                cityCategory,
                description,
                sellerName,
                imagePath,
                imageUrls
        );

        loadSellerAverageRating();
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
            controller.openConversation(advertisementId, sellerLabel.getText());

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

        if (SessionManager.getFullName() != null && SessionManager.getFullName().equals(sellerLabel.getText())){
            return;
        }

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
        ratingController.setAdvertisementId(advertisementId);

        ratingController.setRatingSubmittedListener(
                rating -> loadSellerAverageRating()
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


    private void loadSellerAverageRating() {

        if (sellerId == null) {

            sellerRatingStarsLabel.setText("☆☆☆☆☆");
            averageRatingLabel.setText("0.0");
            return;
        }

        Task<AverageRateResponse> task = new Task<>() {

            @Override
            protected AverageRateResponse call() throws Exception {
                System.out.println("Seller ID = " + sellerId);

                return apiClient.getAverageRating(sellerId);
            }
        };

        task.setOnSucceeded(event -> {

            AverageRateResponse response = task.getValue();

            double average =
                    response == null
                            || response.getAverageRate() == null
                            ? 0.0
                            : response.getAverageRate();

            int roundedAverage =
                    (int) Math.round(average);

            roundedAverage =
                    Math.max(0, Math.min(5, roundedAverage));

            sellerRatingStarsLabel.setText(
                    "★".repeat(roundedAverage)
                            + "☆".repeat(5 - roundedAverage)
            );

            averageRatingLabel.setText(
                    String.format("%.1f", average)
            );
        });

        task.setOnFailed(event -> {

            task.getException().printStackTrace();

            sellerRatingStarsLabel.setText("☆☆☆☆☆");
            averageRatingLabel.setText("0.0");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
