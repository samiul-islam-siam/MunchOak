package com.example.menu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.write;

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

                if (cmd.equals("UPDATE_IMAGE")) {

                    String filename = in.readUTF();
                    int size = in.readInt();
                    byte[] imageBytes = new byte[size];
                    in.readFully(imageBytes);

                    // store in server resources directory
                    File imagesDir = new File("src/main/resources/com/example/manager/images/");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    File target = new File(imagesDir, filename);
                    write(target.toPath(), imageBytes);

                    // broadcast to all connected clients
                    broadcastImage(filename, imageBytes);
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

    private static void broadcastImage(String filename, byte[] data) {
        for (Socket client : clients) {
            try {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                out.writeUTF("UPDATE_IMAGE");
                out.writeUTF(filename);
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

