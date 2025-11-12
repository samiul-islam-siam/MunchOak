package com.example.network;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * One controller for both USER and ADMIN.
 * - USER: single chat with ADMIN
 * - ADMIN: ListView of users, separate chat buffers per user
 */
public class ChatClient {

    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button closeButton;

    // Added for admin view (left list)
    @FXML private ListView<String> userListView; // May be null if not in FXML yet

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    private String username;
    private boolean isAdmin;

    // Admin: per-user conversation buffers for the UI
    private final Map<String, ObservableList<String>> adminConversations = new HashMap<>();
    private String selectedUser = null;

    @FXML
    public void initialize() {
        getIdentityFromSession();
        connectToServer();
        setupSendButton();
        setupCloseButton();
        setupAdminUIBehaviorIfNeeded();
    }

    private void getIdentityFromSession() {
        // From your project session class. Fallbacks included.
        this.username = com.example.manager.Session.getCurrentUsername();
        if (username == null || username.isEmpty()) username = "Guest";

        // Try to read a role; if not available, treat "admin" username as admin
        String role = null;
        try {
            role = com.example.manager.Session.getCurrentRole(); // <-- add this in your Session (String "ADMIN"/"USER")
        } catch (Throwable ignored) {}
        if (role == null || role.isEmpty()) {
            this.isAdmin = "admin".equalsIgnoreCase(username);
        } else {
            this.isAdmin = "ADMIN".equalsIgnoreCase(role);
        }
    }

    // Optionally called externally
    public void setUsername(String username) { this.username = username; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }

    private void setupSendButton() {
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    @FXML
    private void setupCloseButton() {
        closeButton.setOnAction(event -> {
            try {
                if (writer != null) writer.println("BYE");
                closeConnection();
                Stage chatStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                chatStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupAdminUIBehaviorIfNeeded() {
        if (userListView != null) {
            userListView.setVisible(isAdmin);
            userListView.setManaged(isAdmin);

            if (isAdmin) {
                userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                    selectedUser = newV;
                    // Only clear and request history when switching to a different user
                    if (newV != null && !Objects.equals(oldV, newV) && writer != null) {
                        adminConversations.put(newV, FXCollections.observableArrayList()); // fresh buffer
                        chatArea.clear();
                        writer.println("GETHIST|" + newV); // refresh history
                    } else {
                        // Still render whatever we have in memory for same selection
                        renderAdminConversation(newV);
                    }
                });

            }
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5050);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // AUTH first
            writer.println("AUTH|" + username + "|" + (isAdmin ? "ADMIN" : "USER"));

            Thread listener = new Thread(this::listenLoop, "ChatClient-Listener");
            listener.setDaemon(true);
            listener.start();

        } catch (IOException e) {
            appendToActiveArea("Unable to connect to server.\n");
        }
    }

    private void listenLoop() {
        try {
            String msg;
            while ((msg = reader.readLine()) != null) {
                final String line = msg;
                Platform.runLater(() -> handleServerMessage(line));
            }
        } catch (IOException e) {
            Platform.runLater(() -> appendToActiveArea("Disconnected from server.\n"));
        }
    }

    private void handleServerMessage(String msg) {
        if (msg.startsWith("AUTH_OK")) {
            appendToActiveArea("Connected.\n");
        } else if (msg.startsWith("AUTH_ERR|")) {
            appendToActiveArea("Auth failed: " + msg.substring(9) + "\n");
        } else if (msg.startsWith("SYS|")) {
            appendToActiveArea(msg.substring(4) + "\n");
        } else if (msg.startsWith("USER_LIST|")) {
            if (isAdmin && userListView != null) {
                String csv = msg.substring(10);
                List<String> names = new ArrayList<>();
                if (!csv.isEmpty()) names.addAll(Arrays.asList(csv.split(",")));
                userListView.setItems(FXCollections.observableArrayList(names));
                // ensure buffers exist
                for (String u : names) adminConversations.computeIfAbsent(u, k -> FXCollections.observableArrayList());
            }
        } else if (msg.startsWith("INCOMING|")) {
            // INCOMING|<from>|<text>
            String[] p = msg.split("\\|", 3);
            if (p.length >= 3) {
                String from = p[1];
                String text = p[2];

                if (isAdmin) {
                    // route into sender's conversation buffer
                    ObservableList<String> list = adminConversations.computeIfAbsent(from, k -> FXCollections.observableArrayList());
                    list.add(from + ": " + text);
                    if (Objects.equals(selectedUser, from)) renderAdminConversation(from);
                } else {
                    // user: if the 'from' token is "You" or "SELF" (server echo), show "You: ..."
                    if ("You".equalsIgnoreCase(from) || "SELF".equalsIgnoreCase(from)) {
                        appendToActiveArea("You: " + text + "\n");
                    } else {
                        appendToActiveArea(from + ": " + text + "\n");
                    }
                }
            }
        }
        else if (msg.startsWith("HIST|")) {
            String[] p = msg.split("\\|", 3);
            if (p.length >= 3) {
                String u = p[1];
                String line = p[2];
                if (isAdmin) {
                    adminConversations.computeIfAbsent(u, k -> FXCollections.observableArrayList()).add(line);
                    if (Objects.equals(selectedUser, u)) renderAdminConversation(u);
                } else {
                    appendToActiveArea(line + "\n");
                }
            }
        } else if (msg.startsWith("HIST_END|")) {
            // ignore; history already rendered
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty() || writer == null) return;

        if (isAdmin) {
            if (selectedUser == null) {
                appendToActiveArea("Select a user first.\n");
                return;
            }
            writer.println("MSGTO|" + selectedUser + "|" + text);
            adminConversations.computeIfAbsent(selectedUser, k -> FXCollections.observableArrayList())
                    .add("You: " + text);
            renderAdminConversation(selectedUser);
        } else {
            writer.println("MSG|" + text);
            appendToActiveArea("You: " + text + "\n");
        }
        messageField.clear();
    }

    private void renderAdminConversation(String user) {
        if (user == null) {
            chatArea.clear();
            return;
        }
        ObservableList<String> lines = adminConversations.computeIfAbsent(user, k -> FXCollections.observableArrayList());
        chatArea.setText(String.join("\n", lines));
        chatArea.positionCaret(chatArea.getText().length());
    }

    private void appendToActiveArea(String text) {
        chatArea.appendText(text);
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException ignored) {}
    }
}
