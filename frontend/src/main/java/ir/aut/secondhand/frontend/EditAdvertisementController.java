package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import ir.aut.secondhand.frontend.dto.UpdateAdvertisementRequest;
import ir.aut.secondhand.frontend.dto.LocationResponse;
import javafx.concurrent.Task;
import java.math.BigDecimal;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



interface AdvertisementUpdateListener{
    void onAdvertisementUpdated(
            String title,
            String description,
            String category,
            String price,
            String city,
            String imagePath
    );
}

public class EditAdvertisementController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<LocationResponse> locationBox;

    @FXML
    private TilePane imageTilePane;

    @FXML
    private String currentImagePath;

    @FXML
    private Label messageLabel;

    private final List<File> selectedImages = new ArrayList<>();

    private AdvertisementUpdateListener updateListener;

    private Scene previousScene;

    private AdvertisementResponse currentAdvertisement;

    private final ApiClient apiClient = new ApiClient();

    public void setPreviousScene(Scene previousScene){
        this.previousScene = previousScene;
    }

    public void setUpdateListener(AdvertisementUpdateListener updateListener){
        this.updateListener = updateListener;
    }

    @FXML
    public void initialize() {
        categoryBox.getItems().addAll(
                "لوازم الکترونیکی",
                "مبلمان",
                "وسایل نقلیه",
                "پوشاک",
                "سایر"
        );

        loadLocations();
    }

    @FXML
    private void chooseImages() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose Advertisement Images");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        Stage stage = (Stage) imageTilePane.getScene().getWindow();

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            selectedImages.clear();
            selectedImages.addAll(files);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(
                    files.size() + " image(s) selected."
            );
        }
    }

    public void setAdvertisement(
            AdvertisementResponse advertisement
    ) {

        this.currentAdvertisement = advertisement;

        titleField.setText(
                advertisement.getTitle()
        );

        descriptionArea.setText(
                advertisement.getDescription()
        );

        categoryBox.setValue(
                advertisement.getCategoryName()
        );

        categoryBox.setDisable(true);

        priceField.setText(
                advertisement.getPriceAmount() == null
                        ? ""
                        : advertisement.getPriceAmount().toPlainString()
        );

        selectCurrentLocation();

        imageTilePane.getChildren().clear();

        if (advertisement.getImageUrls() == null
                || advertisement.getImageUrls().isEmpty()) {

            currentImagePath = null;
            return;
        }

        String imagePath =
                advertisement.getImageUrls().get(0);

        if (imagePath.startsWith("/")) {
            imagePath =
                    "http://localhost:8080"
                            + imagePath;
        }

        currentImagePath = imagePath;

        Image image =
                new Image(
                        imagePath,
                        true
                );

        ImageView imageView =
                new ImageView(image);

        imageView.setFitWidth(110);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        imageTilePane.getChildren().add(
                imageView
        );
    }

    @FXML
    private void saveChanges() {

        if (currentAdvertisement == null) {

            showEditError(
                    "Advertisement data is not available."
            );

            return;
        }

        String updatedTitle =
                titleField.getText().trim();

        String updatedDescription =
                descriptionArea.getText().trim();

        String updatedPriceText =
                priceField.getText()
                        .trim()
                        .replace(",", "");

        if (updatedTitle.isBlank()
                || updatedDescription.isBlank()
                || updatedPriceText.isBlank()) {

            showEditError(
                    "Please complete title, description and price."
            );

            return;
        }

        if (updatedTitle.length() < 3
                || updatedTitle.length() > 70) {

            showEditError(
                    "Title must contain between 3 and 70 characters."
            );

            return;
        }

        if (updatedDescription.length() < 10
                || updatedDescription.length() > 1000) {

            showEditError(
                    "Description must contain between 10 and 1000 characters."
            );

            return;
        }

        BigDecimal updatedPrice;

        try {

            updatedPrice =
                    new BigDecimal(updatedPriceText);

            if (updatedPrice.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                showEditError(
                        "Price cannot be negative."
                );

                return;
            }

        } catch (NumberFormatException exception) {

            showEditError(
                    "Price must be a valid number."
            );

            return;
        }

        LocationResponse selectedLocation =
                locationBox.getValue();

        if (selectedLocation == null) {

            showEditError(
                    "Please select a location."
            );

            return;
        }

        UpdateAdvertisementRequest request =
                new UpdateAdvertisementRequest(
                        updatedTitle,
                        updatedDescription,
                        updatedPrice,
                        currentAdvertisement
                                .getPriceCurrency() == null
                                ? "IRR"
                                : currentAdvertisement
                                .getPriceCurrency(),
                        currentAdvertisement.getCategoryId(),
                        selectedLocation.getId()
                );

        messageLabel.setStyle(
                "-fx-text-fill: #2563eb;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Saving changes..."
        );

        Task<AdvertisementResponse> task =
                new Task<>() {

                    @Override
                    protected AdvertisementResponse call()
                            throws Exception {

                        AdvertisementResponse updated =
                                apiClient.updateAdvertisement(
                                        currentAdvertisement.getId(),
                                        request
                                );

                        if (!selectedImages.isEmpty()) {

                            apiClient.uploadAdvertisementImages(
                                    currentAdvertisement.getId(),
                                    new ArrayList<>(selectedImages),
                                    0
                            );
                        }

                        return updated;
                    }
                };

        task.setOnSucceeded(event -> {

            messageLabel.setStyle(
                    "-fx-text-fill: #16a34a;"
                            + "-fx-font-size: 14px;"
                            + "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Advertisement updated successfully."
            );

            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(
                            javafx.util.Duration.seconds(1)
                    );

            pause.setOnFinished(
                    finishedEvent ->
                            returnToProfile()
            );

            pause.play();
        });

        task.setOnFailed(event -> {

            Throwable exception =
                    task.getException();

            exception.printStackTrace();

            String message =
                    exception.getMessage();

            if (message == null|| message.isBlank()) {

                message =
                        "Could not update advertisement.";
            }

            showEditError(message);
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    private void showEditError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #dc2626;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void returnToProfile() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/profile-view.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage) titleField
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

            scene.getStylesheets().add(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/css/style.css"
                    ).toExternalForm()
            );

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {

            exception.printStackTrace();

            showEditError(
                    "Advertisement was updated, but Profile could not be opened."
            );
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/profile-view.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) titleField.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);

            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLocations() {

        locationBox.setDisable(true);

        Task<List<LocationResponse>> task =
                new Task<>() {

                    @Override
                    protected List<LocationResponse> call()
                            throws Exception {

                        return apiClient.getLocations();
                    }
                };

        task.setOnSucceeded(event -> {

            locationBox.getItems().clear();

            List<LocationResponse> locations =
                    new ArrayList<>();

            for (LocationResponse location
                    : task.getValue()) {

                collectLocations(
                        location,
                        locations
                );
            }

            locationBox.getItems().addAll(
                    locations
            );

            locationBox.setDisable(false);

            selectCurrentLocation();
        });

        task.setOnFailed(event -> {

            locationBox.setDisable(false);

            showEditError(
                    "Could not load locations."
            );
        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    private void collectLocations(
            LocationResponse location,
            List<LocationResponse> result
    ) {

        if ("CITY".equals(location.getType())
                || "DISTRICT".equals(location.getType())) {

            result.add(location);
        }

        if (location.getSubLocations() != null) {

            for (LocationResponse child
                    : location.getSubLocations()) {

                collectLocations(
                        child,
                        result
                );
            }
        }
    }

    private void selectCurrentLocation() {

        if (currentAdvertisement == null
                || currentAdvertisement.getLocationId() == null) {

            return;
        }

        for (LocationResponse location
                : locationBox.getItems()) {

            if (currentAdvertisement
                    .getLocationId()
                    .equals(location.getId())) {

                locationBox.setValue(location);
                break;
            }
        }
    }
}