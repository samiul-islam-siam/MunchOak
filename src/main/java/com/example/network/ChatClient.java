package com.example.network;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatClient {

    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button closeButton;
    @FXML
    private ListView<ChatMessage> chatListView;
    @FXML
    private ListView<String> userListView;

    private final ObservableList<ChatMessage> chatMessages = FXCollections.observableArrayList();
    private final Map<String, ObservableList<ChatMessage>> adminConversations = new HashMap<>(); // per user conversation history

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    private String username;
    private boolean isAdmin;

    private String selectedUser = null; // which user admin is chatting with
    private boolean loadingHistory = false; // prevent duplication

    @FXML
    public void initialize() {
        getIdentityFromSession();
        setupChatBubbleRenderer();
        chatListView.setItems(chatMessages);
        setupUIEvents();
        setupAdminUIIfNeeded();
        connectToServer();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    private void getIdentityFromSession() {
        this.username = com.example.manager.Session.getCurrentUsername();
        String role = null;
        try {
            role = com.example.manager.Session.getCurrentRole();
        } catch (Exception ignored) {
        }
        this.isAdmin = (role != null) ? role.equalsIgnoreCase("ADMIN") : username.equalsIgnoreCase("admin");
        // (role != null) for NullPointerException
    }

    private void setupUIEvents() {
        sendButton.setOnAction(e -> sendMessage());
        // Send message by clicking Enter key
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendMessage();
        });

        closeButton.setOnAction(e -> {
            try {
                if (writer != null) writer.println("BYE"); // to server
            } catch (Exception ignored) {
            }
            closeConnection();
            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
            st.close();
        });
    }

    private void setupAdminUIIfNeeded() {
        if (userListView == null) return;
        userListView.setVisible(isAdmin);
        userListView.setManaged(isAdmin);

        if (!isAdmin) return; // only admin can see userListView

        userListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> {
                    selectedUser = newV;
                    if (newV == null) return;

                    // Clear previous conversations before loading new
                    adminConversations.putIfAbsent(newV, FXCollections.observableArrayList());
                    chatMessages.setAll(adminConversations.get(newV));

                    // ask server for history of the newly selected user
                    if (!Objects.equals(oldV, newV) && writer != null) {
                        // Mark that history is loading
                        loadingHistory = true;

                        // clear old messages only for this user to prevent echo
                        adminConversations.get(newV).clear();

                        writer.println("GETHIST|" + newV);
                    }
                });
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5050);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // First Message: AUTH|username|ROLE
            writer.println("AUTH|" + username + "|" + (isAdmin ? "ADMIN" : "USER"));

            Thread listener = new Thread(this::listenLoop); // Starts in the background
            listener.setDaemon(true);
            listener.start();
        } catch (IOException e) {
            chatMessages.add(new ChatMessage("Unable to connect to server.", false));
        }
    }

    private void listenLoop() {
        try {
            String msg;
            while ((msg = reader.readLine()) != null) {
                final String line = msg;
                Platform.runLater(() -> handleServerMessage(line)); // as JavaFX UI cannot update from non-UI threads
            }
        } catch (IOException e) {
            Platform.runLater(() -> chatMessages.add(new ChatMessage("Disconnected from server.", false)));
        }
    }

    //-------------------- Server message handler --------------------//
    private void handleServerMessage(String msg) {
        if (msg == null || msg.isEmpty()) return;

        if (msg.startsWith("AUTH_OK")) {
            chatMessages.add(new ChatMessage("Connected", false));
        } else if (msg.startsWith("USER_LIST|") && isAdmin) { // user list for admin
            String[] users = msg.substring(10).split(",");
            ObservableList<String> list = FXCollections.observableArrayList();
            for (String u : users) {
                if (!u.isEmpty()) {
                    list.add(u);
                    adminConversations.putIfAbsent(u, FXCollections.observableArrayList());
                }
            }
            userListView.setItems(list);
        } else if (msg.startsWith("HIST|") && isAdmin) { // HIST|user|ROLE|message
            String[] parts = msg.split("\\|", 4);
            if (parts.length == 4 && selectedUser != null && selectedUser.equals(parts[1])) {
                boolean isSelf = "ADMIN".equalsIgnoreCase(parts[2]);
                adminConversations.get(selectedUser).add(new ChatMessage(parts[3], isSelf));
                chatMessages.setAll(adminConversations.get(selectedUser));
            }
        } else if (msg.startsWith("HIST_END|") && isAdmin) {
            loadingHistory = false;
        } else if (msg.startsWith("INCOMING|")) { // INCOMING|username|TEXT
            String[] p = msg.split("\\|", 3);
            if (p.length < 3) return;

            // Handle messages based on role
            String field1 = p[1], text = p[2];
            if (isAdmin) {
                handleIncomingAsAdmin(field1, text);
            } else {
                handleIncomingAsUser(field1, text);
            }
        } else if (msg.startsWith("SYS|")) { // System messages
            chatMessages.add(new ChatMessage(msg.substring(4), false));
        }
    }

    //-------------------- Admin message handler --------------------//
    // field1 = username, text = message
    private void handleIncomingAsAdmin(String field1, String text) {
        String marker = field1 == null ? "" : field1.trim();

        // Determine if this message is a history entry
        boolean isHistoryRole = marker.equalsIgnoreCase("USER") || marker.equalsIgnoreCase("ADMIN");

        // --- CASE 1: this is a history message ---
        if (loadingHistory && isHistoryRole) {
            if (selectedUser == null) return;

            // Ensure conversation list exists
            adminConversations.putIfAbsent(selectedUser, FXCollections.observableArrayList());
            ObservableList<ChatMessage> conv = adminConversations.get(selectedUser);

            boolean isSelf = marker.equalsIgnoreCase("ADMIN"); // ADMIN entries are self-sent
            conv.add(new ChatMessage(text, isSelf));

            // Update visible chat
            chatMessages.setAll(conv);
            return;
        }

        // --- CASE 2: history just finished ---
        if (loadingHistory && !isHistoryRole) {
            loadingHistory = false; // mark history loading complete
            // Fall through to handle as live message
        }

        // --- CASE 3: live message from a user ---
        // Ensure conversation list exists
        adminConversations.putIfAbsent(marker, FXCollections.observableArrayList());
        ObservableList<ChatMessage> conv = adminConversations.get(marker);

        // Add live message
        conv.add(new ChatMessage(marker + ": " + text, false));

        // Update visible chat if currently selected
        if (Objects.equals(selectedUser, marker)) {
            chatMessages.setAll(conv);
        }
    }

    //-------------------- User message handler --------------------//
    private void handleIncomingAsUser(String field1, String text) {
        String marker = field1 == null ? "" : field1.trim();
        if (marker.equalsIgnoreCase("USER") || marker.equalsIgnoreCase(username)) {
            chatMessages.add(new ChatMessage(text, true));
        } else if (marker.equalsIgnoreCase("ADMIN")) {
            chatMessages.add(new ChatMessage("Admin: " + text, false));
        } else {
            chatMessages.add(new ChatMessage(marker + ": " + text, false));
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;

        if (isAdmin) {
            if (selectedUser == null) {
                chatMessages.add(new ChatMessage("Select a user first.", false));
                messageField.clear();
                return;
            }
            writer.println("MSGTO|" + selectedUser + "|" + text);
            adminConversations.putIfAbsent(selectedUser, FXCollections.observableArrayList());
            adminConversations.get(selectedUser).add(new ChatMessage(text, true));
            chatMessages.setAll(adminConversations.get(selectedUser));
        } else {
            writer.println("MSG|" + text);
            chatMessages.add(new ChatMessage(text, true));
        }
        messageField.clear();
    }

    //-------------------- UI handler --------------------//
    // Right aligned green bubble → self
    // Left aligned white bubble → others
    private void setupChatBubbleRenderer() {
        chatListView.setCellFactory(list -> new ListCell<ChatMessage>() {
            private final HBox box = new HBox();
            private final Label bubble = new Label();

            {
                bubble.setWrapText(true);
                bubble.setMaxWidth(250);
                bubble.setPadding(new Insets(8));
                box.setPadding(new Insets(4));
            }

            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setGraphic(null);
                    return;
                }

                bubble.setText(msg.text);
                if (msg.isSelf) {
                    bubble.setStyle("-fx-background-color:#2ecc71;-fx-text-fill:white;-fx-background-radius:10;");
                    box.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    bubble.setStyle("-fx-background-color:white;-fx-text-fill:black;-fx-background-radius:10;");
                    box.setAlignment(Pos.CENTER_LEFT);
                }

                box.getChildren().setAll(bubble);
                setGraphic(box);
            }
        });
    }

    public void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
    }
}
