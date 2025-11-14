package com.example.network;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClient {

    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button closeButton;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    private String username;

    @FXML
    public void initialize() {
        getUserName();        // ✅ First: ensure username exists
        connectToServer();    // ✅ Then connect
        setupSendButton();
        setupCloseButton();
    }

    private void getUserName() {
        // Get username from session
        username = com.example.manager.Session.getCurrentUsername();

        // Fallback if somehow username is null
        if (username == null || username.isEmpty()) {
            username = "Guest";
        }
    }

    // Add this method in ChatClient
    public void setUsername(String username) {
        this.username = username;
    }


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
                // Close chat connection
                closeConnection();

                // Close the chat window
                Stage chatStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                chatStage.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void connectToServer() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 5050), 5000);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> chatArea.appendText(finalMessage + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                }
            });
            listener.setDaemon(true);
            listener.start();

        } catch (IOException e) {
            Platform.runLater(() -> {
                chatArea.appendText("Unable to connect to server.\n");
                sendButton.setDisable(true);
                messageField.setDisable(true);
            });
        }
    }


    private void sendMessage() {
        if (writer == null) {
            chatArea.appendText("⚠️ Not connected to server.\n");
            return;
        }

        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            writer.println("[" + username + "]: " + text);
            messageField.clear();
        }
    }


    public void closeConnection() {
        try {
            if (writer != null) {
                writer.println("/exit"); // ✅ notify server before disconnecting
            }

            if (socket != null && !socket.isClosed()) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException ignored) {}
    }

}
