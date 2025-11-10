//package com.example.network;
//
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import java.io.*;
//import java.net.Socket;
//
//public class ChatController {
//
//    @FXML
//    private TextArea chatArea;
//    @FXML
//    private TextField messageField;
//
//    private Socket socket;
//    private BufferedReader input;
//    private PrintWriter output;
//    private String username;
//
//    // Called when FXML is loaded
//    @FXML
//    public void initialize() {
//        connectToServer();
//    }
//
//    private void connectToServer() {
//        new Thread(() -> {
//            try {
//                socket = new Socket("localhost", 5050); // Server address
//                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                output = new PrintWriter(socket.getOutputStream(), true);
//
//                // Ask for username
//                Platform.runLater(() -> {
//                    TextInputDialog dialog = new TextInputDialog("Customer");
//                    dialog.setTitle("Enter Name");
//                    dialog.setHeaderText("Join MunchOak Chat");
//                    dialog.setContentText("Name:");
//                    dialog.showAndWait().ifPresent(name -> {
//                        username = name;
//                        output.println(username);
//                    });
//                });
//
//                // Start listener thread
//                new Thread(() -> {
//                    try {
//                        String message;
//                        while ((message = input.readLine()) != null) {
//                            String finalMessage = message;
//                            Platform.runLater(() -> chatArea.appendText(finalMessage + "\n"));
//                        }
//                    } catch (IOException e) {
//                        Platform.runLater(() -> chatArea.appendText("⚠️ Connection lost.\n"));
//                    }
//                }).start();
//
//            } catch (IOException e) {
//                Platform.runLater(() -> chatArea.appendText("❌ Unable to connect to server.\n"));
//            }
//        }).start();
//    }
//}
