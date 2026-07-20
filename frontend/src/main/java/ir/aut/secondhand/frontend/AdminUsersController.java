package ir.aut.secondhand.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ir.aut.secondhand.frontend.dto.AdminUserResponse;
import ir.aut.secondhand.frontend.dto.AdminUsersPageResponse;
import ir.aut.secondhand.frontend.api.ApiClient;

import java.io.IOException;

public class AdminUsersController {

    @FXML
    private TableView<AdminUser> usersTable;

    @FXML
    private TableColumn<AdminUser, String> usernameColumn;

    @FXML
    private TableColumn<AdminUser, String> emailColumn;

    @FXML
    private TableColumn<AdminUser, String> statusColumn;

    @FXML
    private TableColumn<AdminUser, Void> actionColumn;

    @FXML
    private Label messageLabel;

    private final ApiClient apiClient = new ApiClient();

    private final ObservableList<AdminUser> users =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        usernameColumn.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        loadUsers();
        addActionButtons();
    }


    private void addActionButtons() {

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button actionButton =
                            new Button();

                    {
                        actionButton.setOnAction(event -> {

                            AdminUser user =
                                    getTableRow().getItem();

                            if (user == null) {
                                return;
                            }

                            actionButton.setDisable(true);

                            try {

                                AdminUserResponse response =
                                        apiClient.toggleUserBlock(
                                                user.getId()
                                        );

                                user.setStatus(
                                        response.isBlocked()
                                                ? "Blocked"
                                                : "Active"
                                );

                                actionButton.setText(
                                        response.isBlocked()
                                                ? "Activate"
                                                : "Block"
                                );

                                usersTable.refresh();

                                messageLabel.setStyle(
                                        "-fx-text-fill: #16a34a;"
                                );

                                messageLabel.setText(
                                        user.getUsername()
                                                + (
                                                response.isBlocked()
                                                        ? " blocked successfully."
                                                        : " activated successfully."
                                        )
                                );

                            } catch (IOException exception) {

                                exception.printStackTrace();

                                messageLabel.setStyle(
                                        "-fx-text-fill: #dc2626;"
                                );

                                messageLabel.setText(
                                        exception.getMessage() == null
                                                ? "Failed to update user."
                                                : exception.getMessage()
                                );

                            } catch (InterruptedException exception) {

                                Thread.currentThread().interrupt();

                                messageLabel.setStyle(
                                        "-fx-text-fill: #dc2626;"
                                );

                                messageLabel.setText(
                                        "User update was interrupted."
                                );

                            } finally {

                                AdminUser currentUser =
                                        getTableRow().getItem();

                                boolean isCurrentAdmin =
                                        currentUser != null
                                                && currentUser
                                                .getUsername()
                                                .equals(
                                                        SessionManager
                                                                .getUsername()
                                                );

                                actionButton.setDisable(
                                        isCurrentAdmin
                                );
                            }
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            return;
                        }

                        AdminUser user =
                                getTableRow().getItem();

                        if (user == null) {
                            setGraphic(null);
                            return;
                        }

                        boolean isCurrentAdmin =
                                user.getUsername().equals(
                                        SessionManager.getUsername()
                                );

                        actionButton.setDisable(
                                isCurrentAdmin
                        );

                        if ("Active".equals(
                                user.getStatus()
                        )) {

                            actionButton.setText("Block");

                        } else {

                            actionButton.setText("Activate");
                        }

                        setGraphic(actionButton);
                    }
                }
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
                    messageLabel.getScene().getWindow();

            boolean maximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(
                    root,
                    width,
                    height
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

            messageLabel.setText(
                    "Could not open dashboard."
            );
        }
    }
    private void loadUsers() {

        try {

            AdminUsersPageResponse response =
                    apiClient.getAdminUsers(
                            0,
                            50
                    );

            users.clear();

            for (AdminUserResponse userResponse
                    : response.getContent()) {

                AdminUser user = new AdminUser(
                        userResponse.getId(),
                        userResponse.getUsername(),
                        userResponse.getEmail(),
                        userResponse.isBlocked()
                                ? "Blocked"
                                : "Active"
                );

                users.add(user);
            }

            usersTable.setItems(users);

            messageLabel.setStyle(
                    "-fx-text-fill: #16a34a;"
            );

            messageLabel.setText(
                    response.getTotalElements()
                            + " users loaded."
            );

        } catch (IOException exception) {

            exception.printStackTrace();

            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
            );

            messageLabel.setText(
                    exception.getMessage() == null
                            ? "Could not load users."
                            : exception.getMessage()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            messageLabel.setStyle(
                    "-fx-text-fill: #dc2626;"
            );

            messageLabel.setText(
                    "User request was interrupted."
            );
        }
    }
}