package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class AdvertisementDetailsController {

    @FXML
    private ImageView mainImageView;

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
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml"));

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

    @FXML
    public void setAdvertisementDetails(String title, String price, String cityCategory, String description, String imagePath){
        titleLabel.setText(title);
        priceLabel.setText(price);
        cityCategoryLabel.setText(cityCategory);
        descriptionLabel.setText(description);

        mainImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    @FXML
    private void addToFavorites() {
        System.out.println("Add to favorites clicked");
    }

    @FXML
    private void messageSeller() {
        System.out.println("Message seller clicked");
    }
}
