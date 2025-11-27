package com.example.menu;

import com.example.manager.FileStorage;
import com.example.munchoak.FoodItems;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

/**
 * MenuClient: listens for UPDATE_MENU bytes from server and triggers UI refresh.
 * <p>
 * Minimal behavioral change: still writes/reads the same protocol. Now it can
 * call menu.updateView() so the UI refreshes properly.
 */
public class MenuClient {
    // optional observable list (kept for backwards compatibility but not required)
    private ObservableList<FoodItems> foodList;

    // the menu instance whose updateView() will be invoked on update
    private BaseMenu menu;

    private Socket socket;
    private DataOutputStream out;

    /**
     * Default constructor kept for compatibility (creates connection but no menu attached).
     */
    public MenuClient() {
        initSocketAndListener();
    }

    /**
     * Preferred constructor: attach the BaseMenu so the client can trigger menu.updateView().
     */
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

    /**
     * Allows injection of the menu instance after construction (MenuPage will call this).
     */
    public void setMenu(BaseMenu menu) {
        this.menu = menu;
    }

    /**
     * Allows injection of an ObservableList<FoodItems> if some part of UI still wants to be updated directly.
     * Not required if menu.updateView() exists and works.
     */
    public void setFoodList(ObservableList<FoodItems> foodList) {
        this.foodList = foodList;
    }

    private void listenForUpdates() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                String cmd = in.readUTF();

                if ("UPDATE_MENU".equals(cmd)) {
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    // overwrite local menu file
                    File target = FileStorage.getMenuFile();
                    write(target.toPath(), data);

                    // refresh UI on JavaFX thread:
                    Platform.runLater(() -> {
                        try {
                            // Preferred: if we have a menu instance, call its updateView() so it reloads from FileStorage
                            if (menu != null) {
                                menu.updateView();
                                return;
                            }

                            // Fallback for backward-compatibility: update injected ObservableList if present
                            List<FoodItems> newMenu = FileStorage.loadMenu();
                            if (foodList != null) {
                                foodList.setAll(newMenu);
                            }
                            // if neither menu nor foodList is present, nothing more to do
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                else if ("UPDATE_IMAGE".equals(cmd)) {
                    String filename = in.readUTF();
                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    File imagesDir = new File("src/main/resources/com/example/manager/images/");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    File target = new File(imagesDir, filename);
                    write(target.toPath(), data);

                    // Refresh UI (image reload)
                    Platform.runLater(() -> {
                        if (menu != null) {
                            menu.updateView();  // reloads image URL
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMenuUpdate() {
        try {
            byte[] data = readAllBytes(FileStorage.getMenuFile().toPath());
            out.writeUTF("UPDATE_MENU");
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendImageUpdate(File imageFile) {
        try {
            if (imageFile == null || !imageFile.exists()) {
                System.err.println("sendImageUpdate: File does not exist.");
                return;
            }

            byte[] data = readAllBytes(imageFile.toPath());

            out.writeUTF("UPDATE_IMAGE");
            out.writeUTF(imageFile.getName());  // send only name, not full path
            out.writeInt(data.length);
            out.write(data);
            out.flush();

            System.out.println("Image update sent: " + imageFile.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
