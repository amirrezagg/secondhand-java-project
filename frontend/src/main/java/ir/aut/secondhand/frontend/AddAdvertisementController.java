package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
        System.out.println("Choose Image clicked");
    }

    @FXML
    private void submitAdvertisement() {
        System.out.println("Add Advertisement clicked");
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
