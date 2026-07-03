package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

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
    private void goBack() {
        System.out.println("Back clicked");
    }
}
