package ir.aut.secondhand.frontend;

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

public class AdminDashboardController {

    @FXML
    private VBox pendingAdvertisementsContainer;

    @FXML
    private Label pendingCountLabel;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        addMockPendingAdvertisements();
    }

    @FXML
    private void logout() {
        openPage(
                "/ir/aut/secondhand/frontend/fxml/login-view.fxml"
        );
    }

    private void addMockPendingAdvertisements() {

        pendingAdvertisementsContainer.getChildren().clear();

        addPendingAdvertisementCard(
                "لپ‌تاپ ایسوس",
                "45,000,000 تومان",
                "تهران"
        );

        addPendingAdvertisementCard(
                "آیفون 13",
                "50,000,000 تومان",
                "اصفهان"
        );

        addPendingAdvertisementCard(
                "دوچرخه شهری",
                "12,000,000 تومان",
                "کرج"
        );

        pendingCountLabel.setText("3 pending");
    }

    private void addPendingAdvertisementCard(
            String title,
            String price,
            String city
    ) {

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        Label detailsLabel = new Label(
                price + " | " + city
        );

        Button reviewButton = new Button("Review");

        reviewButton.setOnAction(event ->
                openReviewPage()
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
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 16;"
        );

        pendingAdvertisementsContainer
                .getChildren()
                .add(card);
    }

    private void openReviewPage() {

        openPage(
                "/ir/aut/secondhand/frontend/fxml/admin-advertisement-review-view.fxml"
        );
    }

    @FXML
    private void openUsersPage() {

        openPage(
                "/ir/aut/secondhand/frontend/fxml/admin-users-view.fxml"
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

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    "Could not open the requested page."
            );
        }
    }

    @FXML
    private void openCategoriesPage() {

        openPage(
                "/ir/aut/secondhand/frontend/fxml/admin-categories-view.fxml"
        );
    }
}