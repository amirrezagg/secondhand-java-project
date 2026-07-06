package ir.aut.secondhand.frontend;

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

public class AddAdvertisementController {

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
        categoryBox.getItems().addAll(
                "Electronics",
                "Vehicles",
                "Clothing",
                "Furniture",
                "Books",
                "Sports",
                "other"

        );
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
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String price = priceField.getText();
        String city = cityField.getText();
        String category = categoryBox.getValue();

        if (title.isBlank() || description.isBlank() || price.isBlank() || city.isBlank() || category == null){
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
            messageLabel.setText("Please fill in all fields");
            return;
        }

        try {
            Double.parseDouble(price);
        }
        catch (NumberFormatException e){
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");

            messageLabel.setText("Price must be a valid number");
            return;
        }

        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setText("Advertisement submitted....Waiting for admin to approve");


        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        cityField.clear();
        categoryBox.setValue(null);
        selectedImages.clear();
        coverImage = null;
        imageTilePane.getChildren().clear();
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
}
