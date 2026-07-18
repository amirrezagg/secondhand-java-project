package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.TilePane;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;

import java.io.IOException;

public class HomeController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private Button addAdvertisementButton;

    @FXML
    private Button messageButton;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TilePane advertisementTilePane;


    @FXML
    private void openMessages() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/messages-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) advertisementTilePane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm()
            );

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
    private void openFavorites() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/favorites-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) advertisementTilePane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/profile-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) advertisementTilePane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm()
            );

            stage.setScene(scene);

            if (maximized){
                stage.setMaximized(false);

                Platform.runLater(() -> stage.setMaximized(true));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        addMockAdvertisements();
    }

    private void addMockAdvertisements() {
        advertisementTilePane.getChildren().clear();

        advertisementTilePane.getChildren().add(createAdvertisementCard(
                "لپ‌تاپ دست دوم",
                "۴۵ میلیون تومان",
                "تهران",
                "لوازم الکترونیکی",
                "/ir/aut/secondhand/frontend/images/laptop.png"
        ));

        advertisementTilePane.getChildren().add(createAdvertisementCard(
                "صندلی اداری",
                "۸ میلیون تومان",
                "شیراز",
                "مبلمان",
                "/ir/aut/secondhand/frontend/images/chair.png"
        ));

        advertisementTilePane.getChildren().add(createAdvertisementCard(
                "آیفون ۱۲",
                "۵۰ میلیون تومان",
                "اصفهان",
                "لوازم الکترونیکی",
                "/ir/aut/secondhand/frontend/images/iphone.png"
        ));

        advertisementTilePane.getChildren().add(createAdvertisementCard(
                "میز چوبی",
                "۱۲ میلیون تومان",
                "مشهد",
                "مبلمان",
                "/ir/aut/secondhand/frontend/images/table.png"
        ));


    }

    private VBox createAdvertisementCard(
            String title,
            String price,
            String city,
            String category,
            String imagePath
    ) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER_RIGHT);
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 15;"
        );

        Image image = new Image(
                getClass().getResource(imagePath).toExternalForm()
        );

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1E88E5; -fx-font-weight: bold;");

        Label locationLabel = new Label(city + " • " + category);
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        card.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                locationLabel
        );

        card.setOnMouseClicked(mouseEvent -> openAdvertisementDetails(title, price, city + " • " + category, "این آگهی نمونه برای نمایش صفحه جزئیات آگهی است. بعداً اطلاعات واقعی از Backend دریافت می‌شود.", imagePath
        ));

        return card;


    }



    @FXML
    private void logout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/login-view.fxml"));

        Stage stage = (Stage) logoutButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    @FXML
    private void addAdvertisement() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/add-advertisement-view.fxml"));

        Stage stage = (Stage) addAdvertisementButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    private void openAdvertisementDetails(String title, String price, String cityCategory, String description, String imagePath) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/advertisement-details-view.fxml"));

            Parent root = fxmlLoader.load();

            AdvertisementDetailsController controller = fxmlLoader.getController();
            controller.setPreviousPage("home");
            controller.setAdvertisementDetails(title, price, cityCategory, description, imagePath);

            Stage stage = (Stage) advertisementTilePane.getScene().getWindow();

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
}
