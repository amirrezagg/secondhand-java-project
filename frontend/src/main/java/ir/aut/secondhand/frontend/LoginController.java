package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    private Button loginButton;

    @FXML
    private void login() throws IOException{
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()){
            messageLabel.setText("Please fill in all fields");
            return;
        }

        messageLabel.setText("");

        String role;

        if (usernameField.getText().equals("admin")
                && passwordField.getText().equals("admin123")) {

            role = "ADMIN";

        } else {

            role = "USER";
        }

        if ("ADMIN".equals(role)) {

            openPage(
                    "/ir/aut/secondhand/frontend/fxml/admin-dashboard-view.fxml"
            );

        } else {

            openPage(
                    "/ir/aut/secondhand/frontend/fxml/home-view.fxml"
            );
        }
    }

    @FXML
    private void openSignUp() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/signup-view.fxml"));

        Stage stage = (Stage) usernameField.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }


    @FXML
    private void openHome() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);

    }

    @FXML
    private void openAdminDashboard() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource(
                        "/ir/aut/secondhand/frontend/fxml/admin-dashboard-view.fxml"
                )
        );

        Stage stage = (Stage) loginButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(
                getClass().getResource(
                        "/ir/aut/secondhand/frontend/css/style.css"
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    private void openPage(String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    loginButton.getScene().getWindow();

            boolean maximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);

            scene.getStylesheets().add(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/css/style.css"
                    ).toExternalForm()
            );

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {
            exception.printStackTrace();

            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Could not open the requested page.");
        }
    }

}
