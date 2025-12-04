package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.FoodItems;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MenuClient {

    private ObservableList<FoodItems> foodList;
    private BaseMenu menu;

    private Socket socket;
    private DataOutputStream out;

    public MenuClient() {
        initSocketAndListener();
    }

    public MenuClient(BaseMenu menu) {
        this.menu = menu;
        initSocketAndListener();
    }

    private void initSocketAndListener() {
        try {
            socket = new Socket("localhost", 8080);
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(this::listenForUpdates, "MenuClient-Listener").start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMenu(BaseMenu menu) {
        this.menu = menu;
    }

    public void setFoodList(ObservableList<FoodItems> foodList) {
        this.foodList = foodList;
    }

    private void listenForUpdates() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                String cmd = in.readUTF();

                if ("UPDATE_MENU".equals(cmd)) {

                    String filename = in.readUTF();
                    int size = in.readInt();

                    byte[] data = new byte[size];
                    in.readFully(data);

                    // ensure directory exists
                    File dir = new File("src/main/resources/com/example/manager/data/");
                    if (!dir.exists()) dir.mkdirs();

                    File target = new File(dir, filename);
                    write(target.toPath(), data);

                    // IMPORTANT: update FileStorage menu file to the new one
                    FileStorage.setMenuFile(target);

                    Platform.runLater(() -> {
                        try {
                            if (menu != null) {
                                menu.updateView();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else if ("UPDATE_IMAGE".equals(cmd)) {

                    String filename = in.readUTF();
                    int size = in.readInt();

                    byte[] data = new byte[size];
                    in.readFully(data);

                    File imagesDir = new File("src/main/resources/com/example/manager/images/");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    File target = new File(imagesDir, filename);
                    write(target.toPath(), data);

                    Platform.runLater(() -> {
                        if (menu != null) menu.updateView();
                        if (foodList != null) foodList.setAll(FileStorage.loadMenu());
                    });

                } else if ("UPDATE_USERFILE".equals(cmd)) {
                    String filename = in.readUTF();
                    int size = in.readInt();

                    byte[] data = new byte[size];
                    in.readFully(data);

                    File dir = new File("src/main/resources/com/example/manager/data/");
                    if (!dir.exists()) dir.mkdirs();

                    File target = new File(dir, filename);
                    write(target.toPath(), data);

                    System.out.println("User file updated from server.");

                    // (optional) reload FileStorage users list
                    // FileStorage.setUserFile(target);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMenuUpdate() {
        try {
            File menuFile = FileStorage.getMenuFile();   // <-- Automatically read current menu
            byte[] data = readAllBytes(menuFile.toPath());

            out.writeUTF("UPDATE_MENU");
            out.writeUTF(menuFile.getName());  // send filename
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendImageUpdate(File imageFile) {
        try {
            if (!imageFile.exists()) return;

            byte[] data = readAllBytes(imageFile.toPath());

            out.writeUTF("UPDATE_IMAGE");
            out.writeUTF(imageFile.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRegister(String username, String email, String password) {
        try {
            out.writeUTF("REGISTER_USER");   // Command
            out.writeUTF(username);
            out.writeUTF(email);
            out.writeUTF(password);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
