package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminDashboardController {

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private VBox pendingAdvertisementsContainer;

    @FXML
    private Label pendingCountLabel;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        loadPendingAdvertisements();
    }

    @FXML
    private void logout() {
        openPage(
                "/ir/aut/secondhand/frontend/fxml/login-view.fxml"
        );
    }

    private void addPendingAdvertisementCard(
            AdvertisementResponse advertisement
    ) {

        Label titleLabel = new Label(
                advertisement.getTitle()
        );

        titleLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        String priceText =
                advertisement.getPriceAmount() == null
                        ? "No price"
                        : advertisement.getPriceAmount()
                        + " "
                        + advertisement.getPriceCurrency();

        Label detailsLabel = new Label(
                priceText
                        + " | Location ID: "
                        + advertisement.getLocationId()
        );

        Button reviewButton = new Button("Review");

        reviewButton.setOnAction(event ->
                openReviewPage(advertisement)
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox card = new HBox(
                15,
                titleLabel,
                detailsLabel,
                spacer,
                reviewButton
        );

        card.setAlignment(Pos.CENTER_LEFT);

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: #e5e7eb;"
                        + "-fx-border-radius: 10;"
                        + "-fx-padding: 16;"
        );

        pendingAdvertisementsContainer
                .getChildren()
                .add(card);
    }

    private void openReviewPage(
            AdvertisementResponse advertisement
    ) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/"
                                    + "admin-advertisement-review-view.fxml"
                    )
            );

            Parent root = loader.load();

            AdminAdvertisementReviewController controller =
                    loader.getController();

            controller.setAdvertisement(advertisement);

            Stage stage = (Stage)
                    pendingAdvertisementsContainer
                            .getScene()
                            .getWindow();

            boolean maximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

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

            showError(
                    "Could not open advertisement review page."
            );
        }
    }

    @FXML
    private void openUsersPage() {
        openPage(
                "/ir/aut/secondhand/frontend/fxml/admin-users-view.fxml"
        );
    }

    @FXML
    private void openCategoriesPage() {
        openPage(
                "/ir/aut/secondhand/frontend/fxml/admin-categories-view.fxml"
        );
    }

    private void openPage(String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    pendingAdvertisementsContainer
                            .getScene()
                            .getWindow();

            boolean maximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

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

            showError(
                    "Could not open the requested page."
            );
        }
    }

    private void loadPendingAdvertisements() {

        showLoading(
                "Loading pending advertisements..."
        );

        Task<List<AdvertisementResponse>> task =
                new Task<>() {

                    @Override
                    protected List<AdvertisementResponse> call()
                            throws Exception {

                        return apiClient
                                .getPendingAdvertisements();
                    }
                };

        task.setOnSucceeded(event -> {

            List<AdvertisementResponse> advertisements =
                    task.getValue();

            pendingAdvertisementsContainer
                    .getChildren()
                    .clear();

            if (advertisements == null
                    || advertisements.isEmpty()) {

                pendingCountLabel.setText("0 pending");

                messageLabel.setStyle(
                        "-fx-text-fill: #6b7280;"
                );

                messageLabel.setText(
                        "There are no pending advertisements."
                );

                return;
            }

            for (AdvertisementResponse advertisement
                    : advertisements) {

                addPendingAdvertisementCard(
                        advertisement
                );
            }

            pendingCountLabel.setText(
                    advertisements.size() + " pending"
            );

            messageLabel.setText("");
        });

        task.setOnFailed(event -> {

            Throwable exception =
                    task.getException();

            String errorMessage =
                    exception == null
                            || exception.getMessage() == null
                            || exception.getMessage().isBlank()
                            ? "Could not load pending advertisements."
                            : exception.getMessage();

            showError(errorMessage);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showLoading(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #2563eb;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: red;"
                        +"-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }
}