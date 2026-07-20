package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Comparator;

import java.io.IOException;

public class HomeController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryFilterBox;

    @FXML
    private ComboBox<String> cityFilterBox;

    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;

    @FXML
    private ComboBox<String> sortBox;

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

    private final List<Advertisement> advertisements = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        addMockAdvertisements();

        categoryFilterBox.getItems().addAll(
                "لوازم الکترونیکی",
                "مبلمان",
                "پوشاک",
                "کتاب",
                "خودرو",
                "موبایل"
        );

        cityFilterBox.getItems().addAll(
                "تهران",
                "کرج",
                "مشهد",
                "اصفهان",
                "شیراز",
                "تبریز"
        );

        sortBox.getItems().addAll(
                "Newest",
                "Lowest Price",
                "Highest Price"
        );

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        categoryFilterBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        cityFilterBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        minPriceField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        maxPriceField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        sortBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void addMockAdvertisements() {

        advertisements.clear();

        advertisements.add(
                new Advertisement(
                        1,
                        "لپ‌تاپ دست دوم",
                        "لپ‌تاپ سالم و مناسب کارهای روزمره",
                        45_000_000L,
                        "تهران",
                        "لوازم الکترونیکی",
                        "/ir/aut/secondhand/frontend/images/laptop.png",
                        "ACTIVE"
                )
        );

        advertisements.add(
                new Advertisement(
                        2,
                        "صندلی اداری",
                        "صندلی اداری راحت و سالم",
                        8_000_000L,
                        "کرج",
                        "مبلمان",
                        "/ir/aut/secondhand/frontend/images/chair.png",
                        "ACTIVE"
                )
        );

        advertisements.add(
                new Advertisement(
                        3,
                        "آیفون ۱۲",
                        "آیفون ۱۲ سالم با حافظه ۱۲۸ گیگابایت",
                        50_000_000L,
                        "اصفهان",
                        "موبایل",
                        "/ir/aut/secondhand/frontend/images/iphone.png",
                        "ACTIVE"
                )
        );

        advertisements.add(
                new Advertisement(
                        4,
                        "میز چوبی",
                        "میز چوبی مناسب پذیرایی",
                        12_000_000L,
                        "شیراز",
                        "مبلمان",
                        "/ir/aut/secondhand/frontend/images/table.png",
                        "ACTIVE"
                )
        );

        displayAdvertisements(advertisements);
    }

    private void displayAdvertisements(List<Advertisement> advertisementList){

        advertisementTilePane.getChildren().clear();

        for (Advertisement advertisement: advertisementList){
            advertisementTilePane.getChildren().add(createAdvertisementCard(advertisement));
        }
    }

    private VBox createAdvertisementCard( Advertisement advertisement) {
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
                getClass().getResource(advertisement.getImagePath()).toExternalForm()
        );

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

        Label titleLabel = new Label(advertisement.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label(String.format("%,d تومان", advertisement.getPrice()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1E88E5; -fx-font-weight: bold;");

        Label locationLabel = new Label(advertisement.getCity() + " • " + advertisement.getCategory());
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        card.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                locationLabel
        );

        card.setOnMouseClicked(mouseEvent -> openAdvertisementDetails(advertisement.getTitle(), String.format("%,d تومان", advertisement.getPrice()), advertisement.getCity() + " • " + advertisement.getCategory(), "این آگهی نمونه برای نمایش صفحه جزئیات آگهی است. بعداً اطلاعات واقعی از Backend دریافت می‌شود.", advertisement.getImagePath()
        ));

        return card;


    }



    @FXML
    private void logout(ActionEvent event) throws IOException {
        SessionManager.clearSession();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/login-view.fxml"));

        Parent root = fxmlLoader.load();

        Stage stage = (Stage)
                ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

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
    private void applyFilters() {

        String searchText = searchField
                .getText()
                .trim()
                .toLowerCase();

        String selectedCategory =
                categoryFilterBox.getValue();

        String selectedCity =
                cityFilterBox.getValue();

        Long minPrice = null;
        Long maxPrice = null;

        try {
            if (!minPriceField.getText().isBlank()) {
                minPrice = Long.parseLong(
                        minPriceField.getText().trim()
                );
            }

            if (!maxPriceField.getText().isBlank()) {
                maxPrice = Long.parseLong(
                        maxPriceField.getText().trim()
                );
            }

        } catch (NumberFormatException exception) {
            return;
        }

        List<Advertisement> filteredAdvertisements =
                new ArrayList<>();

        for (Advertisement advertisement : advertisements) {

            boolean matchesSearch =
                    searchText.isEmpty()
                            || advertisement.getTitle()
                            .toLowerCase()
                            .contains(searchText)
                            || advertisement.getDescription()
                            .toLowerCase()
                            .contains(searchText);

            boolean matchesCategory =
                    selectedCategory == null
                            || advertisement.getCategory()
                            .equals(selectedCategory);

            boolean matchesCity =
                    selectedCity == null
                            || advertisement.getCity()
                            .equals(selectedCity);

            boolean matchesMinPrice =
                    minPrice == null
                            || advertisement.getPrice() >= minPrice;

            boolean matchesMaxPrice =
                    maxPrice == null
                            || advertisement.getPrice() <= maxPrice;

            if (
                    matchesSearch
                            && matchesCategory
                            && matchesCity
                            && matchesMinPrice
                            && matchesMaxPrice
            ) {
                filteredAdvertisements.add(advertisement);
            }
        }

        if (sortBox.getValue() != null) {

            switch (sortBox.getValue()) {

                case "Lowest Price" -> filteredAdvertisements.sort(
                        Comparator.comparingLong(Advertisement::getPrice)
                );

                case "Highest Price" -> filteredAdvertisements.sort(
                        Comparator.comparingLong(Advertisement::getPrice)
                                .reversed()
                );

                case "Newest" -> filteredAdvertisements.sort(
                        Comparator.comparingLong(Advertisement::getId)
                                .reversed()
                );
            }
        }

        displayAdvertisements(filteredAdvertisements);
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        categoryFilterBox.setValue(null);
        cityFilterBox.setValue(null);
        minPriceField.clear();
        maxPriceField.clear();
        sortBox.setValue(null);

        displayAdvertisements(advertisements);
    }
}
