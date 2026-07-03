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

import java.io.IOException;

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
    private void chooseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Advertisement Image");

        fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imagePreview.getScene().getWindow());

        if (selectedFile != null){
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);

            System.out.println("Selected image: " + selectedFile.getAbsolutePath());
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

        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setText("Advertisement submitted....Waiting for admin to approve");


        titleField.clear();
        descriptionArea.clear();
        priceField.clear();
        cityField.clear();
        categoryBox.setValue(null);
        imagePreview.setImage(null);
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
