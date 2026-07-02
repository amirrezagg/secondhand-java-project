package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void login(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()){
            messageLabel.setText("Please fill in all fields");
            return;
        }

        messageLabel.setText("");

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
    }

    @FXML
    private void openSignUp() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/signup-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 420, 600);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}
