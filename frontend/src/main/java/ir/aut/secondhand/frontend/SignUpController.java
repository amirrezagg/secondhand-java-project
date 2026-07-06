package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

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
    private void signUp(){
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank() || email.isBlank() || phone.isBlank()){
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid email address.");
            return;
        }

        if (!phone.matches("^9\\d{9}$")) {
            messageLabel.setText("Invalid Iranian phone number. Example: 9123456789");
            return;
        }

        String fullPhoneNumber = "+98" + phone;

        if (!password.equals(confirmPassword)){
            messageLabel.setText("Passwords do not match");
            return;
        }

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Successful registration");

        System.out.println("=== New User Registration ===");
        System.out.println("Name: " + name);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + fullPhoneNumber);
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
