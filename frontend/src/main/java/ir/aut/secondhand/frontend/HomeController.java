package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HomeController {

    @FXML
    private TextField searchField;

    @FXML
    private Button addAdvertismentButton;

    @FXML
    private Button messageButton;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void addAdvertisement(){
        System.out.println("Add Advertisement clicked");
    }

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
    private void logout() {
        System.out.println("Logout clicked");
    }
}
