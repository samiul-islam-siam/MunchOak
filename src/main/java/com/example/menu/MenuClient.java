package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.FoodItems;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MenuClient {
    private ObservableList<FoodItems> foodList;
    private Socket socket;
    private DataOutputStream out;

    public MenuClient() {
        try {
            socket = new Socket("localhost", 8080);
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(this::listenForUpdates).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForUpdates() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                String cmd = in.readUTF();

                if (cmd.equals("UPDATE_MENU")) {
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    // overwrite local menu.dat
                    write(Paths.get("src/main/resources/com/example/manager/data/menu_part2.dat"), data);

                    // refresh UI
                    Platform.runLater(() -> {
                        List<FoodItems> newMenu = FileStorage.loadMenu();
                        foodList.setAll(newMenu);
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMenuUpdate() {
        try {
            byte[] data = readAllBytes(Paths.get("src/main/resources/com/example/manager/data/menu_part2.dat"));

            out.writeUTF("UPDATE_MENU");
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

