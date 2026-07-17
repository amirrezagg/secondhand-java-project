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

import java.io.IOException;

public class ProfileController {

    @FXML
    private TilePane myAdvertisementsTilePane;

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

        editButton.setOnAction(event ->
                System.out.println("Edit " + title)
        );

        deleteButton.setOnAction(event ->
                System.out.println("Delete " + title)
        );

        soldButton.setOnAction(event ->
                System.out.println("Mark as sold " + title)
        );

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
        System.out.println("Edit profile clicked");
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
