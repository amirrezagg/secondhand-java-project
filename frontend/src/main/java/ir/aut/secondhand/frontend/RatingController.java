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

import java.awt.*;

public class RatingController {
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

    @FXML
    public void initialize() {

    }

    @FXML
    private void submitRating() {

        if (selectedRating == 0) {
            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;" + "-fx-font-weight: bold;"
            );
            messageLabel.setText("Please select a rating.");
            return;
        }

        String comment = commentArea.getText().trim();

        System.out.println("Selected rating: " + selectedRating);
        System.out.println("Comment: " + comment);

        if (ratingSubmittedListener != null){
            ratingSubmittedListener.accept(selectedRating);
        }

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a; -fx-font-weight: bold;"
        );

        messageLabel.setText(
                "Rating and comment submitted successfully."
        );
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
