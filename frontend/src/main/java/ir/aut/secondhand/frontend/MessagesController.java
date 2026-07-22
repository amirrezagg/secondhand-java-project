package ir.aut.secondhand.frontend;

import ir.aut.secondhand.frontend.api.ApiClient;
import ir.aut.secondhand.frontend.dto.ConversationResponse;
import ir.aut.secondhand.frontend.dto.MessageResponse;
import ir.aut.secondhand.frontend.dto.SendMessageRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MessagesController {

    private final ApiClient apiClient =
            new ApiClient();

    private final ObservableList<ConversationResponse>
            conversations =
            FXCollections.observableArrayList();

    private final ObservableList<MessageResponse>
            displayedMessages =
            FXCollections.observableArrayList();

    private Long selectedConversationId;

    private Long pendingAdvertisementId;
    private String pendingContactName;

    @FXML
    private ListView<ConversationResponse>
            conversationListView;

    @FXML
    private ListView<MessageResponse>
            messageListView;

    @FXML
    private TextField messageField;

    @FXML
    private Label conversationTitleLabel;

    @FXML
    private Label conversationSubtitleLabel;

    @FXML
    private Label onlineStatusLabel;

    @FXML
    public void initialize() {

        conversationListView.setItems(
                conversations
        );

        messageListView.setItems(
                displayedMessages
        );

        configureMessageCells();
        configureConversationCells();
        configureConversationSelection();

        loadConversations();
    }

    private void configureMessageCells() {

        messageListView.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(
                    MessageResponse message,
                    boolean empty
            ) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label messageLabel = new Label(
                        safeText(message.getContent(), "")
                );

                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(420);

                LocalDateTime messageTime =
                        parseDateTime(message.getCreatedAt());

                String currentUserName = SessionManager.getFullName();

                boolean sentByMe =
                        currentUserName != null
                                && currentUserName.equals(message.getSenderName());

                String tick = "";

                if (sentByMe) {
                    tick = "SEEN".equalsIgnoreCase(message.getMessageStatus())
                            ? " ✓✓"
                            : " ✓";
                }

                Label timeLabel = new Label(
                        messageTime.format(
                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                        ) + tick
                );

                if (sentByMe
                        && "SEEN".equalsIgnoreCase(message.getMessageStatus())) {

                    timeLabel.setStyle(
                            "-fx-font-size: 10px;"
                                    + "-fx-text-fill: #2563eb;"
                    );

                } else {

                    timeLabel.setStyle(
                            "-fx-font-size: 10px;"
                                    + "-fx-text-fill: #64748b;"
                    );
                }

                VBox bubble = new VBox(
                        4,
                        messageLabel,
                        timeLabel
                );

                bubble.setMaxWidth(450);
                bubble.setPadding(
                        new Insets(9, 12, 9, 12)
                );

                HBox row = new HBox(bubble);


                if (sentByMe) {
                    row.setAlignment(Pos.CENTER_RIGHT);

                    bubble.setStyle(
                            "-fx-background-color: #dbeafe;"
                                    + "-fx-background-radius: 14;"
                                    + "-fx-border-radius: 14;"
                    );
                } else {
                    row.setAlignment(Pos.CENTER_LEFT);

                    bubble.setStyle(
                            "-fx-background-color: white;"
                                    + "-fx-background-radius: 14;"
                                    + "-fx-border-radius: 14;"
                                    + "-fx-border-color: #e5e7eb;"
                    );
                }

                setText(null);
                setGraphic(row);
            }
        });
    }

    private void configureConversationCells() {

        conversationListView.setCellFactory(
                listView -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            ConversationResponse conversation,
                            boolean empty
                    ) {

                        super.updateItem(
                                conversation,
                                empty
                        );

                        if (empty || conversation == null) {

                            setText(null);
                            setGraphic(null);
                            return;
                        }

                        String contactName =
                                safeText(
                                        conversation.getContactName(),
                                        "Unknown user"
                                );

                        Label nameLabel =
                                new Label(contactName);

                        nameLabel.setStyle(
                                "-fx-font-size: 14px;"
                                        + "-fx-font-weight: bold;"
                                        + "-fx-text-fill: #1f2937;"
                        );

                        String advertisementTitle =
                                safeText(
                                        conversation
                                                .getAdvertisementTitle(),
                                        "Advertisement"
                                );

                        Label advertisementLabel =
                                new Label(
                                        advertisementTitle
                                );

                        advertisementLabel.setStyle(
                                "-fx-font-size: 11px;"
                                        + "-fx-text-fill: #475569;"
                        );

                        String lastMessage =
                                safeText(
                                        conversation.getLastMessage(),
                                        "No messages yet"
                                );

                        if (lastMessage.length() > 28) {

                            lastMessage =
                                    lastMessage.substring(
                                            0,
                                            28
                                    ) + "...";
                        }

                        Label lastMessageLabel =
                                new Label(lastMessage);

                        lastMessageLabel.setStyle(
                                "-fx-font-size: 12px;"
                                        + "-fx-text-fill: #64748b;"
                        );

                        Label timeLabel =
                                new Label(
                                        formatConversationTime(
                                                conversation
                                                        .getLastUpdatedAt()
                                        )
                                );

                        timeLabel.setStyle(
                                "-fx-font-size: 11px;"
                                        + "-fx-text-fill: #94a3b8;"
                        );

                        VBox textBox =
                                new VBox(
                                        3,
                                        nameLabel,
                                        advertisementLabel,
                                        lastMessageLabel
                                );

                        Region spacer =
                                new Region();

                        HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS
                        );

                        HBox row =
                                new HBox(
                                        10,
                                        textBox,
                                        spacer,
                                        timeLabel
                                );

                        row.setAlignment(
                                Pos.CENTER_LEFT
                        );

                        row.setPadding(
                                new Insets(
                                        8,
                                        6,
                                        8,
                                        6
                                )
                        );

                        setText(null);
                        setGraphic(row);
                    }
                }
        );
    }

    private void configureConversationSelection() {

        conversationListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                oldConversation,
                                newConversation
                        ) -> {

                            if (newConversation == null) {
                                return;
                            }

                            selectedConversationId =
                                    newConversation.getId();

                            pendingAdvertisementId =
                                    newConversation
                                            .getAdvertisementId();

                            pendingContactName =
                                    newConversation
                                            .getContactName();

                            conversationTitleLabel.setText(
                                    safeText(newConversation
                                                    .getContactName(),
                                            "Unknown user"
                                    )
                            );

                            conversationSubtitleLabel.setText(
                                    safeText(
                                            newConversation
                                                    .getAdvertisementTitle(),
                                            "Advertisement"
                                    )
                            );

                            onlineStatusLabel.setText("");

                            loadMessages(
                                    selectedConversationId
                            );
                        }
                );
    }

    private void loadConversations() {

        setStatus(
                "Loading conversations...",
                false
        );

        Task<List<ConversationResponse>> task =
                new Task<>() {

                    @Override
                    protected List<ConversationResponse> call()
                            throws Exception {

                        return apiClient
                                .getConversations();
                    }
                };

        task.setOnSucceeded(event -> {

            conversations.setAll(
                    task.getValue() == null
                            ? List.of()
                            : task.getValue()
            );

            setStatus("", false);

            if (conversations.isEmpty()) {

                conversationTitleLabel.setText(
                        pendingContactName == null
                                ? "No conversations"
                                : pendingContactName
                );

                conversationSubtitleLabel.setText(
                        pendingAdvertisementId == null
                                ? "Your conversations will appear here."
                                : "Write a message to start this conversation."
                );

                return;
            }

            selectRequestedConversation();
        });

        task.setOnFailed(event -> {

            task.getException()
                    .printStackTrace();

            conversations.clear();

            setStatus(
                    "Could not load conversations.",
                    true
            );
        });

        startTask(task);
    }

    private void selectRequestedConversation() {

        ConversationResponse matchingConversation = null;

        if (selectedConversationId != null) {

            for (ConversationResponse conversation : conversations) {

                if (selectedConversationId.equals(
                        conversation.getId()
                )) {
                    matchingConversation = conversation;
                    break;
                }
            }
        }


        if (matchingConversation == null
                && pendingAdvertisementId != null) {

            for (ConversationResponse conversation : conversations) {

                if (pendingAdvertisementId.equals(
                        conversation.getAdvertisementId()
                )) {
                    matchingConversation = conversation;
                    break;
                }
            }
        }

        if (matchingConversation != null) {

            conversationListView
                    .getSelectionModel()
                    .select(matchingConversation);

            conversationListView
                    .scrollTo(matchingConversation);

        } else if (!conversations.isEmpty()) {

            conversationListView
                    .getSelectionModel()
                    .selectFirst();
        }
    }

    private void loadMessages(Long conversationId) {

        if (conversationId == null) {
            return;
        }

        displayedMessages.clear();

        setStatus("Loading messages...", false);

        Task<List<MessageResponse>> task = new Task<>() {

            @Override
            protected List<MessageResponse> call() throws Exception {
                return apiClient.getMessages(conversationId);
            }
        };

        task.setOnSucceeded(event -> {
            List<MessageResponse> messages = task.getValue();

            displayedMessages.setAll(messages == null ? List.of() : messages);

            setStatus("", false);
            scrollToLastMessage();
        });

        task.setOnFailed(event -> {

            displayedMessages.clear();

            task.getException().printStackTrace();

            setStatus(
                    "Could not load messages.",
                    true
            );
        });

        startTask(task);
    }

    @FXML
    private void sendMessage() {

        String messageText =
                messageField.getText();

        if (messageText == null
                || messageText.isBlank()) {

            setStatus(
                    "Message cannot be empty.",
                    true
            );

            return;
        }

        if (selectedConversationId == null
                && pendingAdvertisementId == null) {

            setStatus(
                    "Select a conversation first.",
                    true
            );

            return;
        }

        messageField.setDisable(true);

        setStatus(
                "Sending...",
                false
        );

        SendMessageRequest request =
                new SendMessageRequest(
                        selectedConversationId == null
                                ? pendingAdvertisementId
                                : null,
                        messageText.trim()
                );

        Long conversationIdForRequest =
                selectedConversationId;

        Task<MessageResponse> task =
                new Task<>() {

                    @Override
                    protected MessageResponse call()
                            throws Exception {

                        return apiClient.sendMessage(
                                conversationIdForRequest,
                                request
                        );
                    }
                };

        task.setOnSucceeded(event -> {

            messageField.clear();
            messageField.setDisable(false);

            MessageResponse sentMessage = task.getValue();


            if (sentMessage != null) {
                displayedMessages.add(sentMessage);
                scrollToLastMessage();

            }
            setStatus("", false);

            loadConversations();
        });

        task.setOnFailed(event -> {

            messageField.setDisable(false);

            task.getException()
                    .printStackTrace();

            setStatus(
                    extractExceptionMessage(
                            task.getException(),
                            "Could not send message."
                    ),
                    true
            );
        });

        startTask(task);
    }


    public void openConversation(
            Long advertisementId,
            String contactName
    ) {

        pendingAdvertisementId =
                advertisementId;

        pendingContactName =
                contactName;

        selectedConversationId =
                null;

        conversationTitleLabel.setText(
                safeText(
                        contactName,
                        "Seller"
                )
        );

        conversationSubtitleLabel.setText(
                "Write a message to start the conversation."
        );

        if (!conversations.isEmpty()) {
            selectRequestedConversation();
        }
    }


    private void scrollToLastMessage() {

        if (!displayedMessages.isEmpty()) {

            messageListView.scrollTo(
                    displayedMessages.size() - 1
            );
        }
    }

    private LocalDateTime parseDateTime(
            String dateTime
    ) {

        if (dateTime == null
                || dateTime.isBlank()) {

            return LocalDateTime.now();
        }

        try {

            return LocalDateTime.parse(
                    dateTime
            );

        } catch (DateTimeParseException exception) {

            return LocalDateTime.now();
        }
    }

    private String formatConversationTime(
            String dateTime
    ) {

        if (dateTime == null
                || dateTime.isBlank()) {

            return "";
        }

        try {

            LocalDateTime parsed =
                    LocalDateTime.parse(
                            dateTime
                    );

            return parsed.format(
                    DateTimeFormatter.ofPattern(
                            "HH:mm"
                    )
            );

        } catch (DateTimeParseException exception) {

            return "";
        }
    }

    private String safeText(
            String value,
            String fallback
    ) {

        return value == null
                || value.isBlank()
                ? fallback
                : value;
    }

    private void setStatus(
            String message,
            boolean error
    ) {

        onlineStatusLabel.setText(
                message
        );

        onlineStatusLabel.setStyle(
                error
                        ? "-fx-text-fill: #dc2626;"
                        + "-fx-font-weight: bold;": "-fx-text-fill: #64748b;"
        );
    }

    private String extractExceptionMessage(
            Throwable throwable,
            String fallback
    ) {

        if (throwable == null
                || throwable.getMessage() == null
                || throwable.getMessage().isBlank()) {

            return fallback;
        }

        return throwable.getMessage();
    }

    private void startTask(
            Task<?> task
    ) {

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void goBack() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/ir/aut/secondhand/frontend/fxml/home-view.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage) conversationListView
                            .getScene()
                            .getWindow();

            double width =
                    stage.getWidth();

            double height =
                    stage.getHeight();

            boolean maximized =
                    stage.isMaximized();

            Scene scene =
                    new Scene(
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

            setStatus(
                    "Could not return to Home.",
                    true
            );
        }
    }
}