package ir.aut.secondhand.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminCategoriesController {

    @FXML
    private TextField categoryField;

    @FXML
    private ListView<String> categoriesList;

    @FXML
    private Label messageLabel;

    private final ObservableList<String> categories =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        categories.addAll(
                "Electronics",
                "Vehicles",
                "Home",
                "Sports",
                "Fashion"
        );

        categoriesList.setItems(categories);

        categoriesList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue != null) {
                        categoryField.setText(newValue);
                    }

                });
    }

    @FXML
    private void addCategory() {

        String categoryName =
                categoryField.getText().trim();

        if (categoryName.isEmpty()) {

            showError(
                    "Please enter a category name."
            );

            return;
        }

        if (categories.stream().anyMatch(
                category ->
                        category.equalsIgnoreCase(
                                categoryName
                        )
        )) {

            showError(
                    "This category already exists."
            );

            return;
        }

        categories.add(categoryName);
        categoryField.clear();

        showSuccess(
                "Category added successfully."
        );
    }

    @FXML
    private void editCategory() {

        String selectedCategory =
                categoriesList
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedCategory == null) {

            showError(
                    "Please select a category."
            );

            return;
        }

        String newCategoryName =
                categoryField.getText().trim();

        if (newCategoryName.isEmpty()) {

            showError(
                    "Please enter the new category name."
            );

            return;
        }

        if (categories.stream().anyMatch(
                category ->
                        !category.equals(selectedCategory)
                                && category.equalsIgnoreCase(
                                newCategoryName
                        )
        )) {

            showError(
                    "This category already exists."
            );

            return;
        }

        int selectedIndex =
                categoriesList
                        .getSelectionModel()
                        .getSelectedIndex();

        categories.set(
                selectedIndex,
                newCategoryName
        );

        categoryField.clear();

        showSuccess(
                "Category updated successfully."
        );
    }

    @FXML
    private void deleteCategory() {

        String selectedCategory =
                categoriesList
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedCategory == null) {

            showError(
                    "Please select a category."
            );

            return;
        }

        categories.remove(selectedCategory);
        categoryField.clear();

        showSuccess(
                "Category deleted successfully."
        );
    }

    @FXML
    private void backToDashboard() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/fxml/admin-dashboard-view.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    messageLabel
                            .getScene().getWindow();

            boolean maximized =
                    stage.isMaximized();

            Scene scene = new Scene(
                    root,
                    stage.getWidth(),
                    stage.getHeight()
            );

            scene.getStylesheets().add(
                    getClass().getResource(
                            "/ir/aut/secondhand/frontend/css/style.css"
                    ).toExternalForm()
            );

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException exception) {

            exception.printStackTrace();

            showError(
                    "Could not open dashboard."
            );
        }
    }

    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #dc2626;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showSuccess(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                        "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }
}
