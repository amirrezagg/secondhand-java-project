package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.TextInputControl;
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.RegisterResponse;

public class SignUpController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label confirmPasswordErrorLabel;

    @FXML
    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void signUp(){
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        clearErrors();

        boolean hasError = false;

        if (name.isBlank()){
            showError(nameField, nameErrorLabel, "Full name is required.");
            hasError = true;
        }

        if (username.isBlank()){
            showError(usernameField, usernameErrorLabel, "Username is required");
            hasError = true;
        }

        if (email.isBlank()){
            showError(emailField, emailErrorLabel, "Email is required");
            hasError = true;
        }
        else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            showError(emailField, emailErrorLabel, "Invalid email address");
            hasError = true;
        }

        if (phone.isBlank()) {
            showError(phoneField, phoneErrorLabel, "Phone number is required");
            hasError = true;
        }
        else if(!phone.matches("^9\\d{9}$")){
            showError(phoneField, phoneErrorLabel, "Enter a valid phone number");
            hasError = true;
        }

        if (password.isBlank()){
            showError(phoneField, passwordErrorLabel, "Password is required");
            hasError = true;
        }

        if (confirmPassword.isBlank()){
            showError(confirmPasswordField, confirmPasswordErrorLabel, "Please confirm your password");
            hasError = true;
        }
        else if (!password.equals(confirmPassword)){
            showError(confirmPasswordField, confirmPasswordErrorLabel, "Passwords do not match");
            hasError = true;
        }

        if (hasError){
            return;
        }

        String fullPhoneNumber = "+98" + phone;

        try {

            RegisterResponse registerResponse =
                    apiClient.register(
                            username,
                            password,
                            name,
                            fullPhoneNumber,
                            email
                    );

            messageLabel.setStyle(
                    "-fx-text-fill: green;"
            );

            messageLabel.setText(
                    "Successful registration"
            );

            System.out.println(
                    "Registered user: "
                            + registerResponse.getUsername()
            );

        } catch (IOException exception) {

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            String errorMessage =
                    exception.getMessage();

            if (errorMessage == null
                    || errorMessage.isBlank()) {

                errorMessage =
                        "Registration failed.";
            }

            String lowerMessage =
                    errorMessage.toLowerCase();

            if (lowerMessage.contains("username")) {

                showError(
                        usernameField,
                        usernameErrorLabel,
                        errorMessage
                );

            } else if (lowerMessage.contains("email")) {

                showError(
                        emailField,
                        emailErrorLabel,
                        errorMessage
                );

            } else if (lowerMessage.contains("phone")) {

                showError(
                        phoneField,
                        phoneErrorLabel,
                        errorMessage
                );

            } else {

                messageLabel.setText(
                        errorMessage
                );
            }

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    "Registration request was interrupted."
            );
        }
    }


    private void showError(TextInputControl field, Label errorLabel, String message){
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        errorLabel.setText(message);
    }

    private void clearErrors(){
        TextInputControl[] fields = {
                nameField,
                usernameField,
                emailField,
                phoneField,
                passwordField,
                confirmPasswordField
        };

        Label[] labels={
            nameErrorLabel,
            usernameErrorLabel,
            emailErrorLabel,
            phoneErrorLabel,
            passwordErrorLabel,
            confirmPasswordErrorLabel
        };

        for (TextInputControl field: fields){
            field.setStyle("");
        }

        for (Label label: labels){
            label.setText("");
        }

        messageLabel.setText("");
    }

    @FXML
    private void openLogin() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ir/aut/secondhand/frontend/fxml/login-view.fxml"));

        Stage stage = (Stage) messageLabel.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(loader.load(), width, height);

        scene.getStylesheets().add(
                getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }
}
