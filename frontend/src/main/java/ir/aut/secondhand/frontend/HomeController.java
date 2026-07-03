package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private TextField searchField;

    @FXML
    private Button addAdvertisementButton;

    @FXML
    private Button messageButton;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;


    @FXML
    private void openMessages() {
        System.out.println("Open Message clicked");
    }

    @FXML
    private void openFavorites() {
        System.out.println("Open Favorites  clicked");
    }

    @FXML
    private void openProfile() {
        System.out.println("Open profile clicked");
    }

    @FXML
    private void logout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/login-view.fxml"));

        Stage stage = (Stage) logoutButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }

    @FXML
    private void addAdvertisement() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ir/aut/secondhand/frontend/fxml/add-advertisement-view.fxml"));

        Stage stage = (Stage) addAdvertisementButton.getScene().getWindow();

        boolean maximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        scene.getStylesheets().add(getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(maximized);
    }
}
