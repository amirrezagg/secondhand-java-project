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

        addMockUsers();
        addActionButtons();
    }

    private void addMockUsers() {

        users.clear();

        users.addAll(
                new AdminUser(
                        "ali123",
                        "ali@example.com",
                        "Active"
                ),
                new AdminUser(
                        "sara22",
                        "sara@example.com",
                        "Blocked"
                ),
                new AdminUser(
                        "reza77",
                        "reza@example.com",
                        "Active"
                )
        );

        usersTable.setItems(users);
    }

    private void addActionButtons() {

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button actionButton =
                            new Button();

                    {
                        actionButton.setOnAction(event -> {

                            AdminUser user =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if ("Active".equals(user.getStatus())) {

                                user.setStatus("Blocked");

                                messageLabel.setText(
                                        user.getUsername()
                                                + " blocked successfully."
                                );

                            } else {

                                user.setStatus("Active");

                                messageLabel.setText(
                                        user.getUsername()
                                                + " activated successfully."
                                );
                            }

                            usersTable.refresh();
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
                                getTableView()
                                        .getItems()
                                        .get(getIndex());

                        if ("Active".equals(user.getStatus())) {
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
}