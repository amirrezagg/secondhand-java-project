package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import javafx.concurrent.Task;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.ReviewAdvertisementRequest;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import java.util.List;


public class AdminAdvertisementReviewController {

    private final ApiClient apiClient = new ApiClient();
    private AdvertisementResponse advertisement;

    @FXML
    private TilePane advertisementImagesPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label cityLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label sellerLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private TextArea rejectionReasonArea;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    private void goBack() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/admin-dashboard-view.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    messageLabel.getScene().getWindow();

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

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void approveAdvertisement() {

        try {

            apiClient.reviewAdvertisement(
                    advertisement.getId(),
                    new ReviewAdvertisementRequest(
                            "APPROVED",
                            ""
                    )
            );

            messageLabel.setStyle(
                    "-fx-text-fill: green;"
            );

            messageLabel.setText(
                    "Advertisement approved."
            );

        } catch (Exception exception) {

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    exception.getMessage()
            );
        }
    }

    @FXML
    private void rejectAdvertisement() {

        if (advertisement == null
                || advertisement.getId() == null) {

            showError(
                    "Advertisement information is unavailable."
            );
            return;
        }

        String rejectionReason =
                rejectionReasonArea.getText().trim();

        if (rejectionReason.isEmpty()) {

            showError(
                    "Please enter a rejection reason."
            );
            return;
        }

        showLoading("Rejecting advertisement...");

        Task<AdvertisementResponse> task =
                new Task<>() {

                    @Override
                    protected AdvertisementResponse call()
                            throws Exception {

                        ReviewAdvertisementRequest request =
                                new ReviewAdvertisementRequest(
                                        "REJECTED",
                                        rejectionReason
                                );

                        return apiClient.reviewAdvertisement(
                                advertisement.getId(),
                                request
                        );
                    }
                };

        task.setOnSucceeded(event -> {

            showSuccess(
                    "Advertisement rejected successfully."
            );

            /*
             * Return to dashboard.
             * Dashboard reloads pending advertisements from Backend.
             */
            goBack();
        });

        task.setOnFailed(event -> {

            Throwable exception = task.getException();

            String errorMessage =
                    exception == null
                            || exception.getMessage() == null
                            || exception.getMessage().isBlank()
                            ? "Could not reject advertisement."
                            : exception.getMessage();

            showError(errorMessage);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void deleteAdvertisement() {

        if (advertisement == null
                || advertisement.getId() == null) {

            showError(
                    "Advertisement information is unavailable."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle(
                "Delete Advertisement"
        );

        confirmation.setHeaderText(
                "Are you sure you want to delete this advertisement?"
        );

        confirmation.setContentText(
                "This action cannot be undone."
        );

        ButtonType result =
                confirmation.showAndWait()
                        .orElse(ButtonType.CANCEL);

        if (result != ButtonType.OK) {
            return;
        }

        showLoading("Deleting advertisement...");

        Task<Void> task =
                new Task<>() {

                    @Override
                    protected Void call()
                            throws Exception {

                        apiClient.deleteAdvertisement(
                                advertisement.getId()
                        );

                        return null;
                    }
                };

        task.setOnSucceeded(event -> {

            showSuccess(
                    "Advertisement deleted successfully."
            );

            goBack();
        });

        task.setOnFailed(event -> {

            Throwable exception = task.getException();

            String errorMessage =
                    exception == null
                            || exception.getMessage() == null
                            || exception.getMessage().isBlank()
                            ? "Could not delete advertisement."
                            : exception.getMessage();

            showError(errorMessage);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setAdvertisement(AdvertisementResponse advertisement){
        this.advertisement = advertisement;

        titleLabel.setText(advertisement.getTitle());

        priceLabel.setText(advertisement.getPriceAmount() + " " + advertisement.getPriceCurrency());

        cityLabel.setText(
                advertisement.getLocationName()
        );

        categoryLabel.setText(
                advertisement.getCategoryName()
        );

        sellerLabel.setText(
                advertisement.getSellerName()
        );

        descriptionLabel.setText(
                advertisement.getDescription()
        );

        advertisementImagesPane.getChildren().clear();

        List<String> imageUrls =
                advertisement.getImageUrls();

        if (imageUrls != null && !imageUrls.isEmpty()) {

            for (String storedImageUrl : imageUrls) {

                String imageUrl = storedImageUrl;

                if (imageUrl.startsWith("/")) {
                    imageUrl =
                            "http://localhost:8080"
                                    + imageUrl;
                }

                ImageView imageView =
                        new ImageView(
                                new Image(
                                        imageUrl,
                                        true
                                )
                        );

                imageView.setFitWidth(180);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                imageView.setStyle(
                        "-fx-background-color: white;"
                                + "-fx-border-color: #d1d5db;"
                                + "-fx-border-radius: 8;"
                                + "-fx-padding: 5;"
                );

                advertisementImagesPane
                        .getChildren()
                        .add(imageView);
            }
        }
    }

    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #dc2626;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showSuccess(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showLoading(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #2563eb;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }
}
