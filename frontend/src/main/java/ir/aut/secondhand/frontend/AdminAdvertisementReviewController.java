package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;


public class AdminAdvertisementReviewController {

    @FXML
    private ImageView advertisementImageView;

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

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Advertisement approved successfully."
        );
    }

    @FXML
    private void rejectAdvertisement() {

        String rejectionReason =
                rejectionReasonArea.getText().trim();

        if (rejectionReason.isEmpty()) {

            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;" +
                            "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Please enter a rejection reason."
            );

            return;
        }

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Advertisement rejected successfully."
        );
    }

    @FXML
    private void deleteAdvertisement() {

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Advertisement deleted successfully."
        );
    }
}
