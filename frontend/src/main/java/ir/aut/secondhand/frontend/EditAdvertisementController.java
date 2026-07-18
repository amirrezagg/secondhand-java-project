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
    private TextField cityField;

    @FXML
    private TilePane imageTilePane;

    @FXML
    private String currentImagePath;

    @FXML
    private Label messageLabel;

    private final List<File> selectedImages = new ArrayList<>();

    private AdvertisementUpdateListener updateListener;

    private Scene previousScene;

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

    @FXML
    private void saveChanges() {

        String updatedTitle = titleField.getText().trim();
        String updatedDescription = descriptionArea.getText().trim();
        String updatedCategory = categoryBox.getValue();
        String updatedPrice = priceField.getText().trim();
        String updatedCity = cityField.getText().trim();

        boolean hasEmptyField =
                updatedTitle.isEmpty()
                        || updatedDescription.isEmpty()
                        || updatedCategory == null
                        || updatedPrice.isEmpty()
                        || updatedCity.isEmpty();

        if (hasEmptyField) {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Please complete all advertisement fields."
            );

            return;
        }

        String updatedImagePath = currentImagePath;

        if (!selectedImages.isEmpty()) {
            updatedImagePath =
                    selectedImages.get(0).toURI().toString();
        }

        if (updatedImagePath == null
                || updatedImagePath.isBlank()) {

            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Please select an advertisement image."
            );

            return;
        }

        if (updateListener != null) {
            updateListener.onAdvertisementUpdated(
                    updatedTitle,
                    updatedDescription,
                    updatedCategory,
                    updatedPrice,
                    updatedCity,
                    updatedImagePath
            );
        }

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Advertisement updated successfully."
        );

        if (previousScene != null) {
            Stage stage =
                    (Stage) titleField.getScene().getWindow();

            stage.setScene(previousScene);
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

    @FXML
    public void setAdvertisementData(String title, String description, String category, String price, String city, String imagePath){
        titleField.setText(title);
        descriptionArea.setText(description);
        categoryBox.setValue(category);
        priceField.setText(price);
        cityField.setText(city);

        currentImagePath = imagePath;

        imageTilePane.getChildren().clear();

        Image image = new Image(getClass().getResource(imagePath).toExternalForm());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(110);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(false);

        imageTilePane.getChildren().add(imageView);
    }
}