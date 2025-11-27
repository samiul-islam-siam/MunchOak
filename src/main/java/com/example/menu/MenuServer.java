package com.example.menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MenuServer {
    private static final int PORT = 8080;
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                clients.add(client);
                System.out.println("Client connected");

                new Thread(() -> handleClient(client)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client) {
        try {
            DataInputStream in = new DataInputStream(client.getInputStream());

            while (true) {
                String cmd = in.readUTF();

                if (cmd.equals("UPDATE_MENU")) {
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    broadcastMenu(data, client);
                }
            }

        } catch (Exception ignored) {
        } finally {
            clients.remove(client);
        }
    }

    private static void broadcastMenu(byte[] data, Socket exclude) {
        for (Socket client : clients) {
            if (client == exclude) continue;

            try {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                out.writeUTF("UPDATE_MENU");
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            } catch (Exception ignored) {}
        }
    }
}

