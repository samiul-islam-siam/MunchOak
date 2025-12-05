package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.FoodItems;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MenuClient {

    private ObservableList<FoodItems> foodList;
    private BaseMenu menu;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public MenuClient() {
        init();
    }

    public MenuClient(BaseMenu menu) {
        this.menu = menu;
        init();
    }

    private void init() {
        try {
            socket = new Socket("localhost", 8080);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(this::listenLoop, "MenuClient-Listener").start();

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void setMenu(BaseMenu menu) {
        this.menu = menu;
    }

    public void setFoodList(ObservableList<FoodItems> foodList) {
        this.foodList = foodList;
    }

    private void listenLoop() {
        try {
            while (true) {

                String cmd = in.readUTF();

                switch (cmd) {

                    case "UPDATE_MENU" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/data/");
                        dir.mkdirs();

                        File target = new File(dir, filename);
                        write(target.toPath(), data);

                        FileStorage.setMenuFile(target);

                        Platform.runLater(() -> {
                            if (menu != null) menu.updateView();
                        });
                    }

                    case "UPDATE_IMAGE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/images/");
                        dir.mkdirs();

                        File img = new File(dir, filename);
                        write(img.toPath(), data);

                        Platform.runLater(() -> {
                            if (menu != null) menu.updateView();
                            if (foodList != null) foodList.setAll(FileStorage.loadMenu());
                        });
                    }

                    case "UPDATE_USERFILE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("User file synced from server.");
                    }

                    case "REGISTER_RESPONSE" -> {
                        boolean ok = in.readBoolean();
                        String msg = in.readUTF();
                        System.out.println("Server response: " + msg);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Disconnected from server.");
        }
    }

    // -------------------------
    // SEND COMMANDS
    // -------------------------
    public synchronized void sendMenuUpdate() {
        try {
            File menuFile = FileStorage.getMenuFile();
            byte[] data = readAllBytes(menuFile.toPath());

            out.writeUTF("UPDATE_MENU");
            out.writeUTF(menuFile.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public synchronized void sendImageUpdate(File img) {
        try {
            byte[] data = readAllBytes(img.toPath());

            out.writeUTF("UPDATE_IMAGE");
            out.writeUTF(img.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public synchronized void sendRegister(String username, String email, String pwd) {
        try {
            out.writeUTF("REGISTER_USER");
            out.writeUTF(username);
            out.writeUTF(email);
            out.writeUTF(pwd);
            out.flush();

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
