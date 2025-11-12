package com.example.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private CopyOnWriteArrayList<ChatHandler> clients;

    public ChatHandler(Socket socket, CopyOnWriteArrayList<ChatHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            username = input.readLine();
            broadcast("🟢 " + username + " joined the chat");

            String msg;
            while ((msg = input.readLine()) != null) {
                if (msg.equalsIgnoreCase("/exit")) break;
                broadcast(username + ": " + msg);
            }

            broadcast("🔴 " + username + " left the chat");
        } catch (IOException e) {
            System.err.println("User disconnected: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void broadcast(String message) {
        for (ChatHandler client : clients) {
            client.output.println(message);
        }
    }

    private void close() {
        try {
            clients.remove(this);
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}
