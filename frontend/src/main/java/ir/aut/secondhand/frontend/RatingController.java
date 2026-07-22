package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.function.IntConsumer;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.RateUserRequest;
import javafx.concurrent.Task;

import java.awt.*;

public class RatingController {

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private TextArea commentArea;

    @FXML
    private Label messageLabel;

    @FXML
    private Button star1;

    @FXML
    private Button star2;

    @FXML
    private Button star3;

    @FXML
    private Button star4;

    @FXML
    private Button star5;

    @FXML
    private Button backButton;

    private int selectedRating = 0;

    private IntConsumer ratingSubmittedListener;
    public void setRatingSubmittedListener(IntConsumer ratingSubmittedListener){
        this.ratingSubmittedListener = ratingSubmittedListener;
    }

    private Scene previousScene;
    public void setPreviousScene(Scene previousScene){
        this.previousScene = previousScene;
    }

    private Long advertisementId;
    public void setAdvertisementId(Long advertisementId){
        this.advertisementId = advertisementId;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void submitRating() {

        if (advertisementId == null) {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
                            + "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Advertisement ID not found."
            );

            return;
        }

        if (selectedRating == 0) {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
                            + "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Please select a rating."
            );

            return;
        }

        String comment =
                commentArea.getText() == null
                        ? ""
                        : commentArea.getText().trim();

        if (comment.length() > 300) {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
                            + "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Comment must be at most 300 characters."
            );

            return;
        }

        RateUserRequest request =
                new RateUserRequest(
                        advertisementId,
                        selectedRating,
                        comment
                );

        messageLabel.setStyle(
                "-fx-text-fill: #64748b;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Submitting rating..."
        );

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                apiClient.rateSeller(request);

                return null;
            }
        };

        task.setOnSucceeded(event -> {

            messageLabel.setStyle(
                    "-fx-text-fill: #16a34a;"
                            + "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Rating and comment submitted successfully."
            );

            if (ratingSubmittedListener != null) {
                ratingSubmittedListener.accept(
                        selectedRating
                );
            }
        });

        task.setOnFailed(event -> {

            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
                            + "-fx-font-weight: bold;"
            );

            String error =
                    task.getException() == null
                            ? ""
                            : task.getException().getMessage();

            if (error != null
                    && error.toLowerCase().contains(
                    "already submitted"
            )) {

                messageLabel.setText(
                        "You have already rated this advertisement."
                );

            } else if (error != null
                    && error.toLowerCase().contains(
                    "cannot rate yourself"
            )) {

                messageLabel.setText(
                        "You cannot rate your own advertisement."
                );

            } else {

                messageLabel.setText(
                        "Could not submit rating. Please try again."
                );
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void selectOneStar() {
        setRating(1);
    }

    @FXML
    private void selectTwoStars() {
        setRating(2);
    }

    @FXML
    private void selectThreeStars() {
        setRating(3);
    }

    @FXML
    private void selectFourStars() {
        setRating(4);
    }

    @FXML
    private void selectFiveStars() {
        setRating(5);
    }

    private void setRating(int rating) {

        selectedRating = rating;

        Button[] stars = {
                star1,
                star2,
                star3,
                star4,
                star5
        };

        for (int i = 0; i < stars.length; i++) {

            if (i < rating) {
                stars[i].setText("★");
                stars[i].setStyle(
                        "-fx-text-fill: #f59e0b;" +
                                "-fx-font-size: 32px;" +
                                "-fx-background-color: transparent;"
                );

            } else {
                stars[i].setText("☆");
                stars[i].setStyle(
                        "-fx-text-fill: #9ca3af;" +
                                "-fx-font-size: 32px;" +
                                "-fx-background-color: transparent;"
                );
            }
        }
    }

    @FXML
    private void goBack() {
        if (previousScene == null){
            return;
        }

        Stage stage = (Stage) backButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();

        stage.setScene(previousScene);
        stage.setMaximized(maximized);
    }
}
