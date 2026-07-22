package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.CategoryRequest;
import ir.aut.secondhand.frontend.dto.CategoryResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCategoriesController {

    private final ApiClient apiClient = new ApiClient();

    private final ObservableList<CategoryResponse> categories =
            FXCollections.observableArrayList();

    private final ObservableList<CategoryResponse> parentCategories =
            FXCollections.observableArrayList();

    private final Map<Long, Long> parentIdByCategoryId =
            new HashMap<>();

    @FXML
    private TextField categoryField;

    @FXML
    private ListView<CategoryResponse> categoriesList;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<CategoryResponse> parentCategoryComboBox;

    @FXML
    public void initialize() {

        configureCategoryList();
        configureParentComboBox();
        configureSelectionListener();

        loadCategories();
    }

    private void configureCategoryList() {

        categoriesList.setItems(categories);

        categoriesList.setCellFactory(listView ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(
                            CategoryResponse item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        setText(
                                empty || item == null
                                        ? null
                                        : item.getName()
                        );
                    }
                }
        );
    }

    private void configureParentComboBox() {

        parentCategoryComboBox.setItems(parentCategories);

        parentCategoryComboBox.setCellFactory(listView ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(
                            CategoryResponse item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        setText(
                                empty || item == null
                                        ? null
                                        : item.getName()
                        );
                    }
                }
        );

        parentCategoryComboBox.setButtonCell(
                new ListCell<>() {
                    @Override
                    protected void updateItem(
                            CategoryResponse item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        setText(
                                empty || item == null
                                        ? "Select parent category"
                                        : item.getName()
                        );
                    }
                }
        );
    }

    private void configureSelectionListener() {

        categoriesList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue == null) {
                        return;
                    }

                    categoryField.setText(newValue.getName());

                    Long parentId =
                            parentIdByCategoryId.get(newValue.getId());

                    CategoryResponse parent =
                            findCategoryById(parentCategories, parentId);

                    parentCategoryComboBox.setValue(parent);
                });
    }

    @FXML
    private void addCategory() {

        String categoryName = getTrimmedCategoryName();

        if (categoryName.isEmpty()) {
            showError("Please enter a category name.");
            return;
        }

        CategoryResponse selectedParent =
                parentCategoryComboBox.getValue();

        if (selectedParent == null) {
            showError("Please select a parent category.");
            return;
        }

        if (categoryNameExists(categoryName, null)) {
            showError("This category already exists.");
            return;
        }

        CategoryRequest request =
                new CategoryRequest(
                        categoryName,
                        selectedParent.getId()
                );

        Task<CategoryResponse> task = new Task<>() {
            @Override
            protected CategoryResponse call() throws Exception {
                return apiClient.createCategory(request);
            }
        };

        task.setOnSucceeded(event -> {
            clearForm();
            showSuccess("Subcategory added successfully.");
            loadCategories();
        });

        task.setOnFailed(event ->
                showError(extractFriendlyError(
                        task.getException(),
                        "Could not add subcategory."
                ))
        );

        startTask(task);
    }

    @FXML
    private void editCategory() {

        CategoryResponse selectedCategory =
                categoriesList.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showError("Please select a category.");
            return;
        }

        String newCategoryName = getTrimmedCategoryName();

        if (newCategoryName.isEmpty()) {
            showError("Please enter the new category name.");
            return;
        }

        if (categoryNameExists(
                newCategoryName,
                selectedCategory.getId()
        )) {
            showError("This category already exists.");
            return;
        }

        CategoryResponse selectedParent =
                parentCategoryComboBox.getValue();

        if (selectedParent == null) {
            showError("Please select a parent category.");
            return;
        }

        CategoryRequest request =
                new CategoryRequest(
                        newCategoryName,
                        selectedParent.getId()
                );

        Task<CategoryResponse> task = new Task<>() {
            @Override
            protected CategoryResponse call() throws Exception {
                return apiClient.updateCategory(
                        selectedCategory.getId(),
                        request
                );
            }
        };

        task.setOnSucceeded(event -> {
            clearForm();
            showSuccess("Subcategory updated successfully.");
            loadCategories();
        });

        task.setOnFailed(event ->
                showError(extractFriendlyError(
                        task.getException(),
                        "Could not update subcategory."
                ))
        );

        startTask(task);
    }

    @FXML
    private void deleteCategory() {

        CategoryResponse selectedCategory =
                categoriesList.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            showError("Please select a category.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                apiClient.deleteCategory(selectedCategory.getId());
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            clearForm();
            showSuccess("Subcategory deleted successfully.");
            loadCategories();
        });

        task.setOnFailed(event ->
                showError(extractFriendlyError(
                        task.getException(),
                        "Could not delete subcategory."
                ))
        );

        startTask(task);
    }

    private void loadCategories() {

        Task<List<CategoryResponse>> task = new Task<>() {
            @Override
            protected List<CategoryResponse> call() throws Exception {
                return apiClient.getCategories();
            }
        };

        task.setOnSucceeded(event -> {

            List<CategoryResponse> rootCategories = task.getValue();

            parentCategories.clear();
            categories.clear();
            parentIdByCategoryId.clear();

            if (rootCategories == null) {
                return;
            }

            parentCategories.setAll(rootCategories);

            for (CategoryResponse rootCategory : rootCategories) {
                collectSelectableCategories(
                        rootCategory,
                        null,
                        categories
                );
            }
        });

        task.setOnFailed(event ->
                showError(extractFriendlyError(
                        task.getException(),
                        "Failed to load categories."
                ))
        );

        startTask(task);
    }

    private void collectSelectableCategories(
            CategoryResponse category,
            Long parentId,
            List<CategoryResponse> result
    ) {

        if (category == null) {
            return;
        }

        if (Boolean.TRUE.equals(category.getSelectable())) {

            result.add(category);

            if (category.getId() != null && parentId != null) {
                parentIdByCategoryId.put(
                        category.getId(),
                        parentId
                );
            }
        }

        List<CategoryResponse> subCategories =
                category.getSubCategories();

        if (subCategories == null) {
            return;
        }

        for (CategoryResponse subCategory : subCategories) {
            collectSelectableCategories(
                    subCategory,
                    category.getId(),
                    result
            );
        }
    }

    private CategoryResponse findCategoryById(
            List<CategoryResponse> categoryList,
            Long categoryId
    ) {

        if (categoryId == null || categoryList == null) {
            return null;
        }

        for (CategoryResponse category : categoryList) {
            if (category != null
                    && categoryId.equals(category.getId())) {
                return category;
            }
        }

        return null;
    }

    private boolean categoryNameExists(
            String categoryName,
            Long excludedCategoryId
    ) {

        return categories.stream().anyMatch(category ->
                category != null
                        && category.getName() != null
                        && (excludedCategoryId == null
                        || !excludedCategoryId.equals(category.getId()))
                        && category.getName()
                        .equalsIgnoreCase(categoryName)
        );
    }

    private String getTrimmedCategoryName() {

        String text = categoryField.getText();

        return text == null ? "" : text.trim();
    }

    private void clearForm() {

        categoryField.clear();
        categoriesList.getSelectionModel().clearSelection();
        parentCategoryComboBox.getSelectionModel().clearSelection();
    }

    private void startTask(Task<?> task) {

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private String extractFriendlyError(
            Throwable throwable,
            String fallback
    ) {

        if (throwable == null
                || throwable.getMessage() == null
                || throwable.getMessage().isBlank()) {
            return fallback;
        }

        String error = throwable.getMessage();
        String lowerError = error.toLowerCase();

        if (lowerError.contains("already exists")
                || lowerError.contains("duplicate")) {
            return "This category already exists.";
        }

        if (lowerError.contains("integrity")) {
            return "This category could not be changed because it is already in use or conflicts with existing data.";
        }

        if (lowerError.contains("unauthorized")
                || lowerError.contains("forbidden")) {
            return "You are not authorized to manage categories.";
        }

        return fallback;
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
                    messageLabel.getScene().getWindow();

            boolean maximized = stage.isMaximized();

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
            showError("Could not open dashboard.");
        }
    }

    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #dc2626;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }

    private void showSuccess(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: #16a34a;"
                        + "-fx-font-weight: bold;"
        );

        messageLabel.setText(message);
    }
}
