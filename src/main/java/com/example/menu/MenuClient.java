//package com.example.menu;
//
//import com.example.manager.FileStorage;
//import com.example.munchoak.FoodItems;
//import javafx.application.Platform;
//import javafx.collections.ObservableList;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.net.Socket;
//import java.nio.file.Paths;
//import java.util.List;
//
//import static java.nio.file.Files.readAllBytes;
//import static java.nio.file.Files.write;
//
//public class MenuClient {
//    private ObservableList<FoodItems> foodList;
//    private Socket socket;
//    private DataOutputStream out;
//
//
//    public MenuClient() {
//        try {
//            socket = new Socket("localhost", 8080);
//            out = new DataOutputStream(socket.getOutputStream());
//
//            new Thread(this::listenForUpdates).start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void listenForUpdates() {
//        try {
//            DataInputStream in = new DataInputStream(socket.getInputStream());
//
//            while (true) {
//                String cmd = in.readUTF();
//
//                if (cmd.equals("UPDATE_MENU")) {
//                    int size = in.readInt();
//                    byte[] data = new byte[size];
//                    in.readFully(data);
//
//                    // overwrite local menu.dat
//                    File target = FileStorage.getMenuFile();
//                    write(target.toPath(), data);
//                    //write(Paths.get("src/main/resources/com/example/manager/data/menu_part2.dat"), data);
//
//                    // refresh UI
//                    Platform.runLater(() -> {
//                        List<FoodItems> newMenu = FileStorage.loadMenu();
//                        foodList.setAll(newMenu);
//
//                    });
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMenuUpdate() {
//        try {
//            //byte[] data = readAllBytes(Paths.get("src/main/resources/com/example/manager/data/menu_part2.dat"));
//            byte[] data = readAllBytes(FileStorage.getMenuFile().toPath());
//            out.writeUTF("UPDATE_MENU");
//            out.writeInt(data.length);
//            out.write(data);
//            out.flush();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
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
 *
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
}
