package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;

public class ProfileController {

    @FXML
    private TilePane myAdvertisementsTilePane;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    public void initialize() {
        loadMockAdvertisements();
    }

    private void loadMockAdvertisements() {

        myAdvertisementsTilePane.getChildren().clear();

        myAdvertisementsTilePane.getChildren().add(
                createAdvertisementCard(
                        "لپ‌تاپ دست دوم",
                        "۴۵ میلیون تومان",
                        "Pending",
                        "/ir/aut/secondhand/frontend/images/laptop.png"
                )
        );

        myAdvertisementsTilePane.getChildren().add(
                createAdvertisementCard(
                        "صندلی اداری",
                        "۸ میلیون تومان",
                        "Active",
                        "/ir/aut/secondhand/frontend/images/chair.png"
                )
        );

        myAdvertisementsTilePane.getChildren().add(
                createAdvertisementCard(
                        "آیفون ۱۲",
                        "۵۰ میلیون تومان",
                        "Rejected",
                        "/ir/aut/secondhand/frontend/images/iphone.png"
                )
        );

        myAdvertisementsTilePane.getChildren().add(
                createAdvertisementCard(
                        "میز چوبی",
                        "۱۲ میلیون تومان",
                        "Sold",
                        "/ir/aut/secondhand/frontend/images/table.png"
                )
        );
    }

    private VBox createAdvertisementCard(
            String title,
            String price,
            String status,
            String imagePath
    ) {

        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_RIGHT);
        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 14;"
        );

        Image image = new Image(
                getClass().getResource(imagePath).toExternalForm()
        );

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(190);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold;"
        );

        Label priceLabel = new Label(price);
        priceLabel.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #1E88E5;"
        );

        Label statusLabel = new Label("Status: " + status);
        statusLabel.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: bold;"
        );

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button soldButton = new Button("Mark as Sold");

        editButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/ir/aut/secondhand/frontend/fxml/edit-advertisement-view.fxml"
                        )
                );

                Parent root = loader.load();

                EditAdvertisementController controller = loader.getController();

                controller.setAdvertisementData(
                        title,
                        "توضیحات نمونه برای " + title,
                        "لوازم الکترونیکی",
                        price,
                        "تهران",
                        imagePath
                );

                controller.setUpdateListener(
                        (updatedTitel, updatedDescription, updatedcategory, updatedPrice, updatedCity, updatedImagePath) ->{
                            titleLabel.setText(updatedTitel);
                            priceLabel.setText(updatedPrice);

                            try{
                                Image updatedImage;

                                if (updatedImagePath.startsWith("file:")){
                                    updatedImage = new Image(updatedImagePath);
                                }
                                else{
                                    updatedImage = new Image(getClass().getResource(updatedImagePath).toExternalForm());
                                }

                                imageView.setImage(updatedImage);
                            }catch (Exception exception){
                                exception.printStackTrace();
                            }
                        }
                );

                Stage stage = (Stage) myAdvertisementsTilePane.getScene().getWindow();

                double width = stage.getWidth();
                double height = stage.getHeight();
                boolean maximized = stage.isMaximized();

                Scene scene = new Scene(root, width, height);

                scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

                controller.setPreviousScene(stage.getScene());

                stage.setScene(scene);
                stage.setMaximized(maximized);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        deleteButton.setOnAction(event -> {
            myAdvertisementsTilePane.getChildren().remove(card);
        });

        soldButton.setOnAction(event -> {

            statusLabel.setText("Status: Sold");

            card.setStyle(
                    "-fx-background-color: #e5e7eb;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #9ca3af;" +
                            "-fx-border-radius: 12;" +
                            "-fx-padding: 14;"
            );

            titleLabel.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #6b7280;"
            );

            priceLabel.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #6b7280;"
            );

            soldButton.setDisable(true);
            deleteButton.setDisable(true);
            editButton.setDisable(true);
            soldButton.setText("Sold");
        });
        card.getChildren().addAll(
                imageView,
                titleLabel,
                priceLabel,
                statusLabel,
                editButton,
                deleteButton,
                soldButton
        );

        return card;
    }

    @FXML
    private void editProfile() {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Edit Profile");
        dialog.setHeaderText(
                "Update your profile information"
        );

        ButtonType saveButtonType = new ButtonType(
                "Save Changes",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        saveButtonType,
                        ButtonType.CANCEL
                );

        TextField fullNameField =
                new TextField(fullNameLabel.getText());

        TextField usernameField =
                new TextField(usernameLabel.getText());

        TextField emailField =
                new TextField(emailLabel.getText());

        String currentPhone = phoneLabel.getText().trim();

        if (currentPhone.startsWith("+98")){
            currentPhone = currentPhone.substring(3).trim();
        }

        TextField phoneField = new TextField(currentPhone);

        fullNameField.setPromptText("Full name");
        usernameField.setPromptText("Username");
        emailField.setPromptText("Email");
        phoneField.setPromptText("9123456789");

        Label fullNameErrorLabel = createErrorLabel();
        Label usernameErrorLabel = createErrorLabel();
        Label emailErrorLabel = createErrorLabel();
        Label phoneErrorLabel = createErrorLabel();

        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        VBox fullNameBox = new VBox(
                4,
                fullNameField,
                fullNameErrorLabel
        );

        VBox usernameBox = new VBox(
                4,
                usernameField,
                usernameErrorLabel
        );

        VBox emailBox = new VBox(
                4,
                emailField,
                emailErrorLabel
        );

        Label countryCodeLabel = new Label("+98");

        countryCodeLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        HBox phoneInputBox = new HBox(
                8,
                countryCodeLabel,
                phoneField
        );

        phoneInputBox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(phoneField, Priority.ALWAYS);

        VBox phoneBox = new VBox(
                4,
                phoneInputBox,
                phoneErrorLabel
        );

        GridPane grid = new GridPane();

        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Full name:"), 0, 0);
        grid.add(fullNameBox, 1, 0);

        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameBox, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailBox, 1, 2);

        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneBox, 1, 3);

        grid.add(resultLabel, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(520);

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        dialog.getDialogPane().getStyleClass().add("edit-profile-dialog");

        Button saveButton =
                (Button) dialog.getDialogPane()
                        .lookupButton(saveButtonType);

        saveButton.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    clearErrorLabel(fullNameErrorLabel);
                    clearErrorLabel(usernameErrorLabel);
                    clearErrorLabel(emailErrorLabel);
                    clearErrorLabel(phoneErrorLabel);

                    resultLabel.setText("");

                    String fullName =
                            fullNameField.getText().trim();

                    String username =
                            usernameField.getText().trim();

                    String email =
                            emailField.getText().trim();

                    String phone =
                            phoneField.getText().trim();

                    boolean valid = true;

                    if (fullName.isEmpty()) {
                        showError(
                                fullNameErrorLabel,
                                "Full name is required."
                        );

                        valid = false;
                    }

                    if (username.isEmpty()) {
                        showError(
                                usernameErrorLabel,
                                "Username is required."
                        );

                        valid = false;
                    }

                    if (email.isEmpty()) {
                        showError(
                                emailErrorLabel,
                                "Email is required."
                        );

                        valid = false;

                    } else if (!email.matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                    )) {showError(
                            emailErrorLabel,
                            "Enter a valid email address."
                    );

                        valid = false;
                    }

                    if (phone.isEmpty()) {
                        showError(
                                phoneErrorLabel,
                                "Phone number is required."
                        );

                        valid = false;

                    } else if (!phone.matches("^9\\d{9}$"))
                    {
                        showError(
                                phoneErrorLabel,
                                "Enter a valid phone number."
                        );

                        valid = false;
                    }

                    if (!valid) {
                        event.consume();
                        return;
                    }

                    fullNameLabel.setText(fullName);
                    usernameLabel.setText(username);
                    emailLabel.setText(email);
                    phoneLabel.setText("+98 " + phone);

                    resultLabel.setStyle(
                            "-fx-text-fill: #16a34a;" +
                                    "-fx-font-size: 13px;" +
                                    "-fx-font-weight: bold;"
                    );

                    resultLabel.setText(
                            "Information updated successfully."
                    );

                    event.consume();

                    javafx.animation.PauseTransition pause =
                            new javafx.animation.PauseTransition(
                                    javafx.util.Duration.seconds(1)
                            );

                    pause.setOnFinished(
                            finishedEvent -> dialog.close()
                    );

                    pause.play();
                }
        );

        dialog.showAndWait();
    }

    private Label createErrorLabel() {
        Label label = new Label();

        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill: #dc2626;" + "-fx-font-size: 11px;"
        );
        return label;
    }

    private void showError(Label errorLabel, String message){
        errorLabel.setText(message);
    }

    private void clearErrorLabel(Label errorLabel){
        errorLabel.setText("");
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml")
            );

            Parent root = fxmlLoader.load();

            Stage stage = (Stage)
                    myAdvertisementsTilePane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);

            scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
