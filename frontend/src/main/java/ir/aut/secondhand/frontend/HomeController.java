package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import ir.aut.secondhand.frontend.dto.CategoryResponse;
import ir.aut.secondhand.frontend.dto.LocationResponse;
import ir.aut.secondhand.frontend.dto.SearchAdvertisementRequest;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private static final String BACKEND_BASE_URL = "http://localhost:8080";

    private final ApiClient apiClient = new ApiClient();
    private final List<Advertisement> advertisements = new ArrayList<>();
    private final PauseTransition searchDelay =
            new PauseTransition(Duration.millis(400));

    private int searchRequestVersion = 0;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<CategoryResponse> categoryFilterBox;

    @FXML
    private ComboBox<LocationResponse> cityFilterBox;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortBox.getItems().setAll(
                "Newest",
                "Lowest Price",
                "Highest Price"
        );
        sortBox.setValue("Newest");

        searchDelay.setOnFinished(event -> searchAdvertisements());

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> restartSearchDelay()
        );

        categoryFilterBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> searchAdvertisements()
        );

        cityFilterBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> searchAdvertisements()
        );

        minPriceField.textProperty().addListener(
                (observable, oldValue, newValue) -> restartSearchDelay()
        );

        maxPriceField.textProperty().addListener(
                (observable, oldValue, newValue) -> restartSearchDelay()
        );

        sortBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> searchAdvertisements()
        );

        loadCategories();
        loadLocations();
        searchAdvertisements();
    }

    private void restartSearchDelay() {
        searchDelay.stop();
        searchDelay.playFromStart();
    }

    private void searchAdvertisements() {
        String enteredKeyword =
                searchField.getText() == null
                        ? ""
                        : searchField.getText().trim();

        String keywordToSend =
                enteredKeyword.length() >= 3
                        ? enteredKeyword
                        : null;

        Long minPrice;
        Long maxPrice;

        try {
            minPrice = parsePrice(minPriceField.getText());
            maxPrice = parsePrice(maxPriceField.getText());
        } catch (NumberFormatException exception) {
            return;
        }

        if (minPrice != null && minPrice < 0) {
            return;
        }

        if (maxPrice != null && maxPrice < 0) {
            return;
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice > maxPrice) {
            return;
        }

        SearchAdvertisementRequest request =
                new SearchAdvertisementRequest();

        request.setKeyword(keywordToSend);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setSortBy(getSelectedSortOption());
        request.setPage(0);
        request.setSize(100);

        CategoryResponse selectedCategory =
                categoryFilterBox.getValue();

        if (selectedCategory != null) {
            request.setCategoryId(selectedCategory.getId());
        }

        LocationResponse selectedLocation =
                cityFilterBox.getValue();

        if (selectedLocation != null) {
            request.setLocationId(selectedLocation.getId());
        }

        int currentRequestVersion = ++searchRequestVersion;

        Task<List<AdvertisementResponse>> task = new Task<>() {
            @Override
            protected List<AdvertisementResponse> call()
                    throws Exception {
                return apiClient.searchAdvertisements(request);
            }
        };

        task.setOnSucceeded(event -> {
            if (currentRequestVersion != searchRequestVersion) {
                return;
            }

            advertisements.clear();

            List<AdvertisementResponse> responses =
                    task.getValue();

            if (responses != null) {
                for (AdvertisementResponse response : responses) {
                    advertisements.add(
                            convertToAdvertisement(response)
                    );
                }
            }

            displayAdvertisements(advertisements);
        });

        task.setOnFailed(event -> {
            if (currentRequestVersion != searchRequestVersion) {
                return;
            }

            Throwable exception = task.getException();

            if (exception != null) {
                exception.printStackTrace();
            }

            advertisements.clear();
            displayAdvertisements(advertisements);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private Advertisement convertToAdvertisement(
            AdvertisementResponse response
    ) {
        List<String> imageUrls = new ArrayList<>();

        if (response.getImageUrls() != null) {
            for (String storedUrl : response.getImageUrls()) {
                if (storedUrl == null || storedUrl.isBlank()) {
                    continue;
                }

                imageUrls.add(convertToFullImageUrl(storedUrl));
            }
        }

        String imagePath =
                imageUrls.isEmpty()
                        ? null
                        : imageUrls.get(0);

        long price =
                response.getPriceAmount() == null
                        ? 0L
                        : response.getPriceAmount().longValue();

        return new Advertisement(
                response.getId(),
                response.getTitle(),
                response.getDescription(),
                price,
                response.getLocationName(),
                response.getCategoryName(),
                imagePath,
                response.getAdStatus(),
                response.getSellerName(),
                imageUrls,
                response.getSellerId()
        );
    }

    private String convertToFullImageUrl(String storedUrl) {
        String cleanedUrl = storedUrl.trim();

        if (cleanedUrl.startsWith("http://")
                || cleanedUrl.startsWith("https://")) {
            return cleanedUrl;
        }

        if (cleanedUrl.startsWith("/")) {
            return BACKEND_BASE_URL + cleanedUrl;
        }

        return BACKEND_BASE_URL + "/" + cleanedUrl;
    }

    private Long parsePrice(String value)
            throws NumberFormatException {
        if (value == null || value.isBlank()) {
            return null;
        }

        String cleanedValue =
                value.trim()
                        .replace(",", "")
                        .replace(" ", "");

        return Long.parseLong(cleanedValue);
    }

    private String getSelectedSortOption() {
        String selectedSort = sortBox.getValue();

        if (selectedSort == null) {
            return "NEWEST";
        }

        return switch (selectedSort) {
            case "Lowest Price" -> "CHEAPEST";
            case "Highest Price" -> "EXPENSIVE";
            default -> "NEWEST";
        };
    }

    private void loadCategories() {
        Task<List<CategoryResponse>> task = new Task<>() {
            @Override
            protected List<CategoryResponse> call()
                    throws Exception {
                return apiClient.getCategories();
            }
        };

        task.setOnSucceeded(event -> {
            List<CategoryResponse> flattenedCategories =
                    new ArrayList<>();

            flattenCategories(
                    task.getValue(),
                    flattenedCategories
            );

            categoryFilterBox.getItems().setAll(
                    flattenedCategories
            );
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();

            if (exception != null) {
                exception.printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void flattenCategories(
            List<CategoryResponse> source,
            List<CategoryResponse> destination
    ) {
        if (source == null) {
            return;
        }

        for (CategoryResponse category : source) {
            if (category == null || category.getId() == null) {
                continue;
            }

            if (category.getSelectable() == null
                    || category.getSelectable()) {
                destination.add(category);
            }

            flattenCategories(
                    category.getSubCategories(),
                    destination
            );
        }
    }

    private void loadLocations() {
        Task<List<LocationResponse>> task = new Task<>() {
            @Override
            protected List<LocationResponse> call()
                    throws Exception {
                return apiClient.getLocations();
            }
        };

        task.setOnSucceeded(event -> {
            List<LocationResponse> cities =
                    new ArrayList<>();

            flattenCities(task.getValue(), cities);

            cityFilterBox.getItems().setAll(cities);
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();

            if (exception != null) {
                exception.printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void flattenCities(
            List<LocationResponse> source,
            List<LocationResponse> destination
    ) {
        if (source == null) {
            return;
        }

        for (LocationResponse location : source) {
            if (location == null) {
                continue;
            }

            if (location.getId() != null
                    && location.getType() != null
                    && location.getType()
                    .equalsIgnoreCase("CITY")) {
                destination.add(location);
            }

            flattenCities(
                    location.getSubLocations(),
                    destination
            );
        }
    }

    private void displayAdvertisements(
            List<Advertisement> advertisementList
    ) {
        advertisementTilePane.getChildren().clear();

        if (advertisementList == null
                || advertisementList.isEmpty()) {
            Label emptyLabel =
                    new Label("No advertisements found.");

            emptyLabel.setStyle(
                    "-fx-font-size: 16px;"
                            + "-fx-text-fill: #6b7280;"
            );

            advertisementTilePane
                    .getChildren()
                    .add(emptyLabel);

            return;
        }

        for (Advertisement advertisement
                : advertisementList) {
            advertisementTilePane
                    .getChildren()
                    .add(
                            createAdvertisementCard(
                                    advertisement
                            )
                    );
        }
    }

    private VBox createAdvertisementCard(
            Advertisement advertisement
    ) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER_RIGHT);
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #e5e7eb;"
                        + "-fx-border-radius: 12;"
                        + "-fx-padding: 15;"
        );

        String imagePath =
                advertisement.getImagePath();

        if (imagePath != null && !imagePath.isBlank()) {
            Image image = loadImage(imagePath);

            if (image != null) {
                ImageView imageView =
                        new ImageView(image);

                imageView.setFitWidth(190);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(false);

                card.getChildren().add(imageView);
            }
        }

        Label titleLabel =
                new Label(
                        safeText(advertisement.getTitle())
                );

        titleLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        Label priceLabel =
                new Label(
                        String.format(
                                "%,d تومان",
                                advertisement.getPrice()
                        )
                );

        priceLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #1E88E5;"
                        + "-fx-font-weight: bold;"
        );

        String city =
                safeText(advertisement.getCity());

        String category =
                safeText(advertisement.getCategory());

        String cityCategory;

        if (!city.isBlank() && !category.isBlank()) {
            cityCategory = city + " • " + category;
        } else if (!city.isBlank()) {
            cityCategory = city;
        } else {
            cityCategory = category;
        }

        Label locationLabel =
                new Label(cityCategory);

        locationLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #6b7280;"
        );

        card.getChildren().addAll(
                titleLabel,
                priceLabel,
                locationLabel
        );

        card.setOnMouseClicked(mouseEvent ->
                openAdvertisementDetails(
                        advertisement.getId(),
                        advertisement.getSellerId(),
                        advertisement.getTitle(),
                        String.format(
                                "%,d تومان",
                                advertisement.getPrice()
                        ),
                        cityCategory,
                        advertisement.getDescription(),
                        advertisement.getSellerName(),
                        advertisement.getImagePath(),
                        advertisement.getImageUrls()
                )
        );

        return card;
    }

    private Image loadImage(String imagePath) {
        try {
            if (imagePath.startsWith("http://")
                    || imagePath.startsWith("https://")) {
                return new Image(
                        imagePath,
                        true
                );
            }

            URL resource =
                    getClass().getResource(imagePath);

            if (resource == null) {
                return null;
            }

            return new Image(resource.toExternalForm());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    @FXML
    private void clearFilters() {
        searchDelay.stop();

        searchField.clear();
        categoryFilterBox.setValue(null);
        cityFilterBox.setValue(null);
        minPriceField.clear();
        maxPriceField.clear();
        sortBox.setValue("Newest");

        searchAdvertisements();
    }

    @FXML
    private void openMessages() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/messages-view.fxml"
                    )
            );

            Parent root = fxmlLoader.load();

            Stage stage =
                    (Stage) advertisementTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(
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

            if (maximized) {
                stage.setMaximized(false);

                Platform.runLater(
                        () -> stage.setMaximized(true)
                );
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void openFavorites() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/favorites-view.fxml"
                    )
            );

            Parent root = fxmlLoader.load();

            Stage stage =
                    (Stage) advertisementTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(
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
        }
    }

    @FXML
    private void openProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/profile-view.fxml"
                    )
            );

            Parent root = fxmlLoader.load();

            Stage stage =
                    (Stage) advertisementTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(
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

            if (maximized) {
                stage.setMaximized(false);

                Platform.runLater(
                        () -> stage.setMaximized(true)
                );
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event)
            throws IOException {
        SessionManager.clearSession();

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource(
                                "/ir/aut/secondhand/frontend/fxml/login-view.fxml"
                        )
                );

        Parent root = fxmlLoader.load();

        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        boolean maximized = stage.isMaximized();

        Scene scene = new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );

        scene.getStylesheets().add(
                getClass().getResource(
                        "/ir/aut/secondhand/frontend/css/style.css"
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    @FXML
    private void addAdvertisement()
            throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource(
                                "/ir/aut/secondhand/frontend/fxml/add-advertisement-view.fxml"
                        )
                );

        Stage stage =
                (Stage) addAdvertisementButton
                        .getScene()
                        .getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(
                fxmlLoader.load(),
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
    }

    private void openAdvertisementDetails(
            Long advertisementId,
            Long sellerId,
            String title,
            String price,
            String cityCategory,
            String description,
            String sellerName,
            String imagePath,
            List<String> imageUrls
    ) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/advertisement-details-view.fxml"
                            )
                    );

            Parent root = fxmlLoader.load();

            AdvertisementDetailsController controller =
                    fxmlLoader.getController();

            controller.setPreviousPage("home");

            controller.setAdvertisementDetails(
                    advertisementId,
                    sellerId,
                    title,
                    price,
                    cityCategory,
                    description,
                    sellerName,
                    imagePath,
                    imageUrls
            );

            Stage stage =
                    (Stage) advertisementTilePane
                            .getScene()
                            .getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(
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
        }
    }
}
