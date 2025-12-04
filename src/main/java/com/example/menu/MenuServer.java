package com.example.menu;

import com.example.manager.FileStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MenuServer {

    private static final int PORT = 8080;
    private static final List<Socket> clients = new ArrayList<>();

    private static byte[] latestMenu = null;
    private static String latestMenuName = "menu.dat";
    private static final Map<String, byte[]> latestImages = new HashMap<>();
    private static byte[] latestUserFile = null;


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Menu Server started on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                clients.add(client);
                System.out.println("Client connected.");

                // send latest menu to late joiners
                if (latestMenu != null) {
                    try {
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        out.writeUTF("UPDATE_MENU");
                        out.writeUTF(latestMenuName);
                        out.writeInt(latestMenu.length);
                        out.write(latestMenu);
                    } catch (Exception ignored) {}
                }

                // send all previously stored images
                for (var entry : latestImages.entrySet()) {
                    try {
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        out.writeUTF("UPDATE_IMAGE");
                        out.writeUTF(entry.getKey());
                        out.writeInt(entry.getValue().length);
                        out.write(entry.getValue());
                    } catch (Exception ignored) {}
                }

                // send latest user file to late joiners
                if (latestUserFile != null) {
                    try {
                        DataOutputStream out = new DataOutputStream(client.getOutputStream());
                        out.writeUTF("UPDATE_USERFILE");
                        out.writeUTF("users.dat");
                        out.writeInt(latestUserFile.length);
                        out.write(latestUserFile);
                        out.flush();
                    } catch (Exception ignored) {}
                }


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

                if ("UPDATE_MENU".equals(cmd)) {
                    String filename = in.readUTF();
                    int size = in.readInt();

                    byte[] data = new byte[size];
                    in.readFully(data);

                    // save for late clients
                    latestMenuName = filename;
                    latestMenu = data;

                    // 🔥 WRITE new menu file to disk
                    File menuDir = new File("src/main/resources/com/example/manager/data/");
                    if (!menuDir.exists()) menuDir.mkdirs();
                    write(new File(menuDir, filename).toPath(), data);

                    broadcastMenu(filename, data, client);
                }

                if ("UPDATE_IMAGE".equals(cmd)) {
                    String filename = in.readUTF();
                    int size = in.readInt();

                    byte[] img = new byte[size];
                    in.readFully(img);

                    File imgDir = new File("src/main/resources/com/example/manager/images/");
                    if (!imgDir.exists()) imgDir.mkdirs();

                    write(new File(imgDir, filename).toPath(), img);

                    // store for late clients
                    latestImages.put(filename, img);

                    broadcastImage(filename, img);
                }

                if ("REGISTER_USER".equals(cmd)) {
                    String username = in.readUTF();
                    String email = in.readUTF();
                    String pwd = in.readUTF();

                    // Write into server's user file
                    File userFile = new File("src/main/resources/com/example/manager/data/users.dat");
                    if (!userFile.exists()) userFile.createNewFile();

                    FileStorage.appendUser(username, email, pwd);

                    // Read whole file so it can be broadcast to all clients
                    byte[] fullUserData = readAllBytes(userFile.toPath());

                    // Store the latest version for late joiners
                    latestUserFile = fullUserData;

                    // Broadcast to all clients
                    broadcastUserFile("users.dat", fullUserData);

                    // Also respond to this client if you want confirmation
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());
                    out.writeUTF("REGISTER_RESPONSE");
                    out.writeBoolean(true);
                    out.writeUTF("Registration successful.");
                    out.flush();
                }

            }

        } catch (Exception ignored) {
            clients.remove(client);
        }
    }

    private static void broadcastMenu(String filename, byte[] data, Socket exclude) {
        for (Socket c : clients) {
            if (c == exclude) continue;

            try {
                DataOutputStream out = new DataOutputStream(c.getOutputStream());
                out.writeUTF("UPDATE_MENU");
                out.writeUTF(filename);
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            } catch (Exception ignored) {}
        }
    }

    private static void broadcastImage(String filename, byte[] data) {
        for (Socket c : clients) {
            try {
                DataOutputStream out = new DataOutputStream(c.getOutputStream());
                out.writeUTF("UPDATE_IMAGE");
                out.writeUTF(filename);
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            } catch (Exception ignored) {}
        }
    }

    private static void broadcastUserFile(String filename, byte[] data) {
        for (Socket c : clients) {
            try {
                DataOutputStream out = new DataOutputStream(c.getOutputStream());
                out.writeUTF("UPDATE_USERFILE");
                out.writeUTF(filename);
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            } catch (Exception ignored) {}
        }
    }
}
