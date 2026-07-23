package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.dto.CategoryResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import javafx.scene.layout.TilePane;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import ir.aut.secondhand.frontend.dto.CreateAdvertisementRequest;
import ir.aut.secondhand.frontend.dto.LocationResponse;
import javafx.concurrent.Task;
import javafx.scene.control.ListCell;
import java.math.BigDecimal;

public class AddAdvertisementController {

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<CategoryResponse> categoryBox;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<LocationResponse> locationBox;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private TilePane imageTilePane;

    private List<File> selectedImages = new ArrayList<>();
    private File coverImage;

    @FXML
    public void initialize() {
        configureCategoryBox();
        loadCategories();
        loadLocations();
    }

    @FXML
    private void chooseImages() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Advertisement Images");

        fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(imageTilePane.getScene().getWindow());

        if (files == null || files.isEmpty()){
            return;
        }

        selectedImages.clear();
        selectedImages.addAll(files);

        coverImage = selectedImages.get(0);

        refreshImagePreview();

        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setText(files.size() + "images selected. First image is cover");
    }

    private void refreshImagePreview() {

        imageTilePane.getChildren().clear();

        for (File file : selectedImages) {

            VBox imageBox = new VBox();
            imageBox.setSpacing(5);
            imageBox.setAlignment(javafx.geometry.Pos.CENTER);

            ImageView imageView = new ImageView(
                    new Image(file.toURI().toString())
            );

            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Label coverLabel = new Label();

            if (file.equals(coverImage)) {
                coverLabel.setText("Main Image");
                coverLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                imageBox.setStyle("-fx-border-color: #22c55e; -fx-border-width: 3; -fx-padding: 5;");
            } else {
                coverLabel.setText("Click to set main");
                coverLabel.setStyle("-fx-text-fill: #6b7280;");
                imageBox.setStyle("-fx-border-color: transparent; -fx-border-width: 3; -fx-padding: 5;");
            }

            imageBox.setOnMouseClicked(event -> {
                coverImage = file;
                refreshImagePreview();

                messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
                messageLabel.setText("Main image selected.");
            });

            imageBox.getChildren().addAll(imageView, coverLabel);
            imageTilePane.getChildren().add(imageBox);
        }
    }

    @FXML
    private void submitAdvertisement() {

        String title =
                titleField.getText().trim();

        String description =
                descriptionArea.getText().trim();

        String priceText =
                priceField.getText().trim();

        LocationResponse selectedLocation = locationBox.getValue();

        CategoryResponse selectedCategory = categoryBox.getValue();

        if (title.isBlank()
                || description.isBlank()
                || priceText.isBlank()
                || selectedLocation == null
                || selectedCategory == null) {

            showError("Please fill in all fields.");
            return;
        }

        if (title.length() < 3) {
            showError(
                    "Title must contain at least 3 characters."
            );
            return;
        }

        if (title.length() > 70) {
            showError(
                    "Title must not exceed 70 characters."
            );
            return;
        }

        if (description.length() < 10) {
            showError(
                    "Description must contain at least 10 characters."
            );
            return;
        }

        if (description.length() > 1000) {
            showError(
                    "Description must not exceed 1000 characters."
            );
            return;
        }

        BigDecimal price;

        try {
            price = new BigDecimal(priceText);

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                showError(
                        "Price cannot be negative."
                );
                return;
            }

        } catch (NumberFormatException exception) {
            showError(
                    "Price must be a valid number."
            );
            return;
        }

        Long categoryId = selectedCategory.getId();
        Long locationId = selectedLocation.getId();

        CreateAdvertisementRequest request =
                new CreateAdvertisementRequest(
                        title,
                        description,
                        price,
                        "IRR",
                        categoryId,
                        locationId
                );

        showLoading(
                "Submitting advertisement..."
        );

        List<File> imagesToUpload =
                new ArrayList<>(selectedImages);

        int calculatedMainImageIndex =
                coverImage == null
                        ? 0
                        : imagesToUpload.indexOf(coverImage);

        final int mainImageIndex =
                calculatedMainImageIndex < 0
                        ? 0
                        : calculatedMainImageIndex;

        Task<AdvertisementResponse> task =
                new Task<>() {

                    @Override
                    protected AdvertisementResponse call()
                            throws Exception {

                        AdvertisementResponse createdAdvertisement =
                                apiClient.createAdvertisement(request);

                        if (!imagesToUpload.isEmpty()) {

                            apiClient.uploadAdvertisementImages(
                                    createdAdvertisement.getId(),
                                    imagesToUpload,
                                    mainImageIndex
                            );
                        }

                        return createdAdvertisement;
                    }
                };

        task.setOnSucceeded(event -> {

            AdvertisementResponse response =
                    task.getValue();

            String imageMessage =
                    imagesToUpload.isEmpty()
                            ? " No images uploaded."
                            : " "
                            + imagesToUpload.size()
                            + " image(s) uploaded.";

            showSuccess(
                    "Advertisement submitted successfully. "
                            + "Status: "
                            + response.getAdStatus()
                            + "."
                            + imageMessage
            );

            clearForm();
        });

        task.setOnFailed(event -> {

            Throwable exception =
                    task.getException();

            String errorMessage =
                    exception.getMessage();

            if (errorMessage == null
                    || errorMessage.isBlank()) {

                errorMessage =
                        "Advertisement could not be submitted.";
            }

            showError(errorMessage);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: red;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showSuccess(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: green;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showLoading(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #2563eb;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void clearForm() {

        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        locationBox.setValue(null);
        categoryBox.setValue(null);

        selectedImages.clear();
        coverImage = null;

        imageTilePane
                .getChildren()
                .clear();
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml"));

        Stage stage = (Stage) backButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    private void loadCategories() {

        categoryBox.setDisable(true);
        showLoading("Loading categories...");

        Task<List<CategoryResponse>> task =
                new Task<>() {

                    @Override
                    protected List<CategoryResponse> call()
                            throws Exception {

                        return apiClient.getCategories();
                    }
                };

        task.setOnSucceeded(event -> {

            categoryBox.getItems().clear();

            List<CategoryResponse> allCategories =
                    new ArrayList<>();

            for (CategoryResponse category : task.getValue()) {
                collectAllCategories(
                        category,
                        allCategories
                );
            }

            categoryBox.getItems().clear();

            categoryBox.getItems().addAll(
                    allCategories
            );

            categoryBox.setDisable(false);
            messageLabel.setText("");
        });

        task.setOnFailed(event -> {

            categoryBox.setDisable(false);

            String errorMessage =
                    task.getException() == null
                            || task.getException().getMessage() == null
                            ? "Could not load categories."
                            : task.getException().getMessage();

            showError(errorMessage);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void collectAllCategories(
            CategoryResponse category,
            List<CategoryResponse> result
    ) {

        if (category == null) {
            return;
        }

        result.add(category);

        if (category.getSubCategories() == null) {
            return;
        }

        for (
                CategoryResponse subCategory
                : category.getSubCategories()
        ) {
            collectAllCategories(
                    subCategory,
                    result
            );
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

            List<LocationResponse> selectableLocations =
                    new ArrayList<>();

            for (LocationResponse location
                    : task.getValue()) {

                collectSelectableLocations(
                        location,
                        selectableLocations
                );
            }

            locationBox.getItems().addAll(
                    selectableLocations
            );

            locationBox.setDisable(false);
        });

        task.setOnFailed(event -> {

            locationBox.setDisable(false);

            Throwable exception =
                    task.getException();

            showError(
                    exception == null
                            || exception.getMessage() == null
                            ? "Could not load locations."
                            : exception.getMessage()
            );
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void collectSelectableLocations(
            LocationResponse location,
            List<LocationResponse> result
    ) {

        if ("CITY".equals(location.getType())
                || "DISTRICT".equals(location.getType())) {

            result.add(location);
        }

        if (location.getSubLocations() != null) {

            for (LocationResponse subLocation
                    : location.getSubLocations()) {

                collectSelectableLocations(
                        subLocation,
                        result
                );
            }
        }
    }

    private void configureCategoryBox() {

        categoryBox.setCellFactory(listView ->
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            CategoryResponse category,
                            boolean empty
                    ) {
                        super.updateItem(category, empty);

                        if (empty || category == null) {
                            setText(null);
                            setDisable(false);
                            setStyle("");
                            return;
                        }

                        setText(
                                getCategoryDisplayName(category)
                        );

                        boolean selectable =
                                Boolean.TRUE.equals(
                                        category.getSelectable()
                                );

                        setDisable(!selectable);

                        if (selectable) {
                            setStyle(
                                    "-fx-text-fill: #111827;"
                            );
                        } else {
                            setStyle(
                                    "-fx-text-fill: #6b7280;"
                                            + "-fx-font-weight: bold;"
                                            + "-fx-background-color: #f3f4f6;"
                            );
                        }
                    }
                }
        );

        categoryBox.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            CategoryResponse category,
                            boolean empty
                    ) {
                        super.updateItem(category, empty);

                        if (empty || category == null) {
                            setText(null);
                        } else {
                            setText(category.getName());
                        }
                    }
                }
        );
    }

    private String getCategoryDisplayName(
            CategoryResponse category
    ) {

        if (category.getParentId() == null) {
            return category.getName();
        }

        return "    └ " + category.getName();
    }


}
