package ir.aut.secondhand.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MessagesController {
    @FXML
    private ListView<String> conversationListView;

    @FXML
    private ListView<MessageItem> messageListView;

    @FXML
    private TextField messageField;

    @FXML
    private Label conversationTitleLabel;

    @FXML
    private Label conversationSubtitleLabel;

    @FXML
    private final Map<String, ObservableList<MessageItem>> conversationMessages = new HashMap<>();

    @FXML
    private String selectedConversation;

    @FXML
    private Label onlineStatusLabel;

    @FXML
    public void initialize() {

        conversationListView.getItems().addAll(
                "Amirreza", "Ehsan", "Sepeher");


        conversationMessages.put(
                "Amirreza",
                FXCollections.observableArrayList(
                        new MessageItem(
                                "سلام، لپ‌تاپ هنوز موجوده؟",
                                false,
                                LocalDateTime.now().minusMinutes(25),
                                true
                        ),
                        new MessageItem(
                                "بله، هنوز موجوده.",
                                true,
                                LocalDateTime.now().minusMinutes(20),
                                true
                        )
                )
        );

        conversationMessages.put(
                "Ehsan",
                FXCollections.observableArrayList(
                        new MessageItem(
                                "قیمت نهایی صندلی چقدره؟",
                                false,
                                LocalDateTime.now().minusHours(1),
                                true
                        )
                )
        );

        conversationMessages.put(
                "Sepeher",
                FXCollections.observableArrayList()
        );

        messageListView.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(MessageItem message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label messageLabel = new Label(message.getText());
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(420);

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");

                String status = "";

                if (message.isSentByMe()) {
                    status = message.isSeen() ? "  ✓✓" : "  ✓";
                }

                Label timeLabel = new Label(
                        formatter.format(message.getDateTime()) + status
                );

                timeLabel.setStyle(
                        "-fx-font-size: 10px; -fx-text-fill: #64748b;"
                );

                VBox bubble = new VBox(4, messageLabel, timeLabel);
                bubble.setMaxWidth(450);
                bubble.setPadding(new Insets(9, 12, 9, 12));

                HBox row = new HBox(bubble);

                if (message.isSentByMe()) {
                    row.setAlignment(Pos.CENTER_RIGHT);

                    bubble.setStyle(
                            "-fx-background-color: #dbeafe;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-border-radius: 14;"
                    );
                } else {
                    row.setAlignment(Pos.CENTER_LEFT);

                    bubble.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-border-color: #e5e7eb;"
                    );
                }

                setText(null);
                setGraphic(row);
            }
        });

        conversationListView.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(String conversationName, boolean empty) {
                super.updateItem(conversationName, empty);

                if (empty || conversationName == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label nameLabel = new Label(conversationName);
                nameLabel.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #1f2937;"
                );

                ObservableList<MessageItem> messages =
                        conversationMessages.get(conversationName);

                String lastMessageText = "No messages yet";
                String lastMessageTime = "";

                if (messages != null && !messages.isEmpty()) {
                    MessageItem lastMessage =
                            messages.get(messages.size() - 1);

                    lastMessageText = lastMessage.getText();

                    if (lastMessageText.length() > 28) {
                        lastMessageText =
                                lastMessageText.substring(0, 28) + "...";
                    }

                    DateTimeFormatter timeFormatter =
                            DateTimeFormatter.ofPattern("HH:mm");

                    lastMessageTime =
                            timeFormatter.format(lastMessage.getDateTime());
                }

                Label lastMessageLabel = new Label(lastMessageText);
                lastMessageLabel.setStyle(
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: #64748b;"
                );

                Label timeLabel = new Label(lastMessageTime);
                timeLabel.setStyle(
                        "-fx-font-size: 11px;" +
                                "-fx-text-fill: #94a3b8;"
                );

                VBox textBox = new VBox(4, nameLabel, lastMessageLabel);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox conversationRow = new HBox(
                        10,
                        textBox,
                        spacer,
                        timeLabel
                );

                conversationRow.setAlignment(Pos.CENTER_LEFT);
                conversationRow.setPadding(
                        new Insets(8, 6, 8, 6)
                );

                setText(null);
                setGraphic(conversationRow);
            }
        });

        conversationListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue == null) {
                        return;
                    }

                    selectedConversation = newValue;

                    conversationTitleLabel.setText(newValue);
                    conversationSubtitleLabel.setText("Online");


                    messageListView.setItems(
                            conversationMessages.get(newValue)
                    );
                });
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/ir/aut/secondhand/frontend/fxml/home-view.fxml"));

            Parent root = fxmlLoader.load();

            Stage stage = (Stage) conversationListView.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean maximized = stage.isMaximized();

            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(
                    getClass().getResource("/ir/aut/secondhand/frontend/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(maximized);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {

        if (selectedConversation == null) {
            return;
        }

        String messageText = messageField.getText();

        if (messageText == null || messageText.isBlank()) {
            return;
        }

        MessageItem newMessage = new MessageItem(messageText, true, LocalDateTime.now(), false);

        conversationMessages.get(selectedConversation).add(newMessage);

        messageField.clear();

        messageListView.scrollTo(messageListView.getItems().size() - 1);

        conversationListView.refresh();
    }

    public void openConversation(String conversationName){
        if (conversationName == null || conversationName.isBlank()){
            return;
        }

        if (!conversationListView.getItems().contains(conversationName)){
            conversationListView.getItems().add(conversationName);

            conversationMessages.put(conversationName, FXCollections.observableArrayList());
        }

        conversationListView.getSelectionModel().select(conversationName);
        conversationListView.scrollTo(conversationName);
    }
}
