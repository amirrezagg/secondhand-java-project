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
import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.LoginResponse;

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
    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void login() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    "Please fill in all fields"
            );

            return;
        }

        messageLabel.setText("");
        loginButton.setDisable(true);

        try {

            LoginResponse loginResponse =
                    apiClient.login(username, password);

            SessionManager.startSession(
                    loginResponse.getToken(),
                    loginResponse.getUsername(),
                    loginResponse.getFullName(),
                    loginResponse.getRole()
            );

            System.out.println(
                    "Logged in user: "
                            + loginResponse.getUsername()
            );

            System.out.println(
                    "Role: "
                            + loginResponse.getRole()
            );


            if ("ADMIN".equalsIgnoreCase(
                    loginResponse.getRole()
            )) {

                openPage(
                        "/ir/aut/secondhand/frontend/fxml/admin-dashboard-view.fxml"
                );

            } else {

                openPage(
                        "/ir/aut/secondhand/frontend/fxml/home-view.fxml"
                );
            }

        } catch (IOException exception) {

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    exception.getMessage()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            messageLabel.setText(
                    "Login request was interrupted."
            );

        } finally {

            loginButton.setDisable(false);
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
