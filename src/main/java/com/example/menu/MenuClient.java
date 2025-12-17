package com.example.menu;

import com.example.manager.FileStorage;
import com.example.manager.Session;
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
                    case "UPDATE_RESERVATIONS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        //FileStorage.reloadReservationStatus(); // ✅ ADD THIS
                        System.out.println("Reservations synced from server.");
                        Platform.runLater(() -> Session.notifyReservationUpdated()); // ✅ ADD
                    }
                    case "UPDATE_RESERVATION_STATUS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        Platform.runLater(() -> Session.notifyReservationUpdated());
                    }
                    case "UPDATE_MESSAGES" -> {

                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/example/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Messages synced from server.");

                        Platform.runLater(() -> Session.notifyMessageUpdated());
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

    public synchronized void sendMessageUpdate() {
        try {
            File msgFile =
                    new File("src/main/resources/com/example/manager/data/messages.dat");

            if (!msgFile.exists()) return;

            byte[] data = readAllBytes(msgFile.toPath());

            out.writeUTF("UPDATE_MESSAGES");
            out.writeUTF(msgFile.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();

        } catch (Exception e) {
            System.err.println("Message sync error: " + e.getMessage());
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

    public synchronized void sendRegister(String username, String email,String contactNo, String pwd) {
        try {
            out.writeUTF("REGISTER_USER");
            out.writeUTF(username);
            out.writeUTF(email);
            out.writeUTF(contactNo);
            out.writeUTF(pwd);
            out.flush();

        } catch (Exception e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
//    public synchronized void sendReservationUpdate() {
//        try {
//            File resFile = new File("src/main/resources/com/example/manager/data/reservations.dat");
//            byte[] data = readAllBytes(resFile.toPath());
//
//            out.writeUTF("UPDATE_RESERVATIONS");
//            out.writeUTF(resFile.getName());
//            out.writeInt(data.length);
//            out.write(data);
//            out.flush();
//
//        } catch (Exception e) {
//            System.err.println("IOException: " + e.getMessage());
//        }
//    }
public synchronized void sendReservationUpdate() {
    try {
        File resFile = new File("src/main/resources/com/example/manager/data/reservations.dat");
        File statusFile = new File("src/main/resources/com/example/manager/data/reservation_status.dat");

        byte[] resData = readAllBytes(resFile.toPath());
        byte[] statusData = statusFile.exists()
                ? readAllBytes(statusFile.toPath())
                : new byte[0];

        // send reservations
        out.writeUTF("UPDATE_RESERVATIONS");
        out.writeUTF(resFile.getName());
        out.writeInt(resData.length);
        out.write(resData);

        // send status file
        out.writeUTF("UPDATE_RESERVATION_STATUS");
        out.writeUTF(statusFile.getName());
        out.writeInt(statusData.length);
        out.write(statusData);

        out.flush();

    } catch (Exception e) {
        System.err.println("IOException: " + e.getMessage());
    }
}


}

