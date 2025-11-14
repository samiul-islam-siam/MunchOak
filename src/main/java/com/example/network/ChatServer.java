package com.example.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 5050;
    private static final String HISTORY_FILE = "src/main/resources/com/example/network/chat_history.txt"; //
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static final List<String> chatHistory = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // ✅ Load old messages when the server starts
        loadChatHistory();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("💬 Chat Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("✅ New client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Broadcast message to all clients (For Testing)
    static void broadcast(String message) {
        chatHistory.add(message);
        saveMessageToFile(message); // ✅ also save it
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // ✅ Send chat history to a new client
    static void sendChatHistory(ClientHandler client) {
        for (String msg : chatHistory) {
            client.sendMessage(msg);
        }
    }

    // ✅ Load chat history from file on startup
    private static void loadChatHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    chatHistory.add(line);
                }
                System.out.println("📜 Loaded " + chatHistory.size() + " previous messages.");
            } catch (IOException e) {
                System.err.println("⚠️ Error loading chat history: " + e.getMessage());
            }
        }
    }

    // ✅ Save each new message to file
    private static void saveMessageToFile(String message) {
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("⚠️ Error saving message: " + e.getMessage());
        }
    }

    // ==========================================================
    // 🧩 Inner class for handling clients
    // ==========================================================
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // ✅ Send all old messages to the new client
                sendChatHistory(this);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("📩 " + message);
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("❌ Client disconnected");
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                clients.remove(this);
            }
        }

        void sendMessage(String message) {
            if (out != null) {
                try {
                    out.println(message);
                } catch (Exception e) {
                    System.err.println("Failed to send message to client: " + e.getMessage());
                }
            }
        }

    }
}
