package com.munchoak.server;

import com.munchoak.manager.MenuStorage;
import com.munchoak.manager.Session;
import com.munchoak.manager.StoragePaths;
import com.munchoak.menu.BaseMenu;
import com.munchoak.mainpage.FoodItems;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

public class MainClient {

    private ObservableList<FoodItems> foodList;
    private BaseMenu menu;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public MainClient() {
        init();
    }

    public MainClient(BaseMenu menu) {
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

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File target = new File(dir, filename);
                        write(target.toPath(), data);

                        StoragePaths.setMenuFile(target);

                        Platform.runLater(() -> {
                            if (menu != null) menu.updateView();
                        });
                    }

                    case "UPDATE_IMAGE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/images/");
                        dir.mkdirs();

                        File img = new File(dir, filename);
                        write(img.toPath(), data);

                        Platform.runLater(() -> {
                            if (menu != null) menu.updateView();
                            if (foodList != null) foodList.setAll(MenuStorage.loadMenu());
                        });
                    }

                    case "UPDATE_USERFILE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("User file synced from server.");
                    }

                    case "UPDATE_ADMINFILE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Admin file synced from server.");
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

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
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

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
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

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Messages synced from server.");

                        Platform.runLater(() -> Session.notifyMessageUpdated());
                    }

                    case "UPDATE_COUPONS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();
                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();
                        write(new File(dir, filename).toPath(), data);

                        System.out.println("Coupons synced.");
                        Platform.runLater(() -> Session.notifyCouponUpdated());
                    }

                    case "UPDATE_COUPON_USAGE" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();
                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();
                        write(new File(dir, filename).toPath(), data);

                        System.out.println("Coupon usage synced.");
                    }

                    case "UPDATE_PAYMENTS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Payments file synced from server.");
                    }

                    case "UPDATE_CARTS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Carts file synced from server.");
                    }

                    case "UPDATE_CARTITEMS" -> {
                        String filename = in.readUTF();
                        int size = in.readInt();

                        byte[] data = new byte[size];
                        in.readFully(data);

                        File dir = new File("src/main/resources/com/munchoak/manager/data/");
                        dir.mkdirs();

                        File f = new File(dir, filename);
                        write(f.toPath(), data);

                        System.out.println("Cart items file synced from server.");
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
            File menuFile = StoragePaths.getMenuFile();
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
                    new File("src/main/resources/com/munchoak/manager/data/messages.dat");

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

    public synchronized void sendRegister(String username, String email, String contactNo, String pwd) {
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

    public synchronized void sendCouponUpdate() {
        try {
            File couponFile = StoragePaths.COUPONS_FILE;
            File usageFile = StoragePaths.COUPON_USAGE_FILE;

            if (couponFile.exists()) {
                byte[] data = readAllBytes(couponFile.toPath());
                out.writeUTF("UPDATE_COUPONS");
                out.writeUTF(couponFile.getName());
                out.writeInt(data.length);
                out.write(data);
            }

            if (usageFile.exists()) {
                byte[] data = readAllBytes(usageFile.toPath());
                out.writeUTF("UPDATE_COUPON_USAGE");
                out.writeUTF(usageFile.getName());
                out.writeInt(data.length);
                out.write(data);
            }

            out.flush();

        } catch (Exception e) {
            System.err.println("Coupon sync error: " + e.getMessage());
        }
    }

    public synchronized void sendReservationUpdate() {
        try {
            File resFile = new File("src/main/resources/com/munchoak/manager/data/reservations.dat");
            File statusFile = new File("src/main/resources/com/munchoak/manager/data/reservation_status.dat");

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

    public synchronized void sendUserFileUpdate() {
        try {
            File userFile = StoragePaths.USERS_FILE;
            if (!userFile.exists()) return;

            byte[] data = readAllBytes(userFile.toPath());

            out.writeUTF("UPDATE_USERFILE");
            out.writeUTF(userFile.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (Exception e) {
            System.err.println("User file sync error: " + e.getMessage());
        }
    }

    public synchronized void sendAdminFileUpdate() {
        try {
            File adminFile = StoragePaths.ADMIN_FILE;
            if (!adminFile.exists()) return;

            byte[] data = readAllBytes(adminFile.toPath());

            out.writeUTF("UPDATE_ADMINFILE");
            out.writeUTF(adminFile.getName());
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (Exception e) {
            System.err.println("Admin file sync error: " + e.getMessage());
        }
    }

    public synchronized void sendPaymentFileUpdate() {
        try {
            File paymentsFile = StoragePaths.PAYMENT_MASTER_FILE; // payments.dat

            if (paymentsFile.exists()) {
                byte[] data = readAllBytes(paymentsFile.toPath());
                out.writeUTF("UPDATE_PAYMENTS");
                out.writeUTF(paymentsFile.getName());
                out.writeInt(data.length);
                out.write(data);
            }

            out.flush();

        } catch (Exception e) {
            System.err.println("Payment sync error: " + e.getMessage());
        }
    }

    public synchronized void sendCartFilesUpdate() {
        try {
            File cartsFile = StoragePaths.CARTS_FILE;             // carts.dat
            File cartItemsFile = StoragePaths.CART_ITEMS_FILE;    // cartitems.dat

            if (cartsFile.exists()) {
                byte[] data = readAllBytes(cartsFile.toPath());
                out.writeUTF("UPDATE_CARTS");
                out.writeUTF(cartsFile.getName());
                out.writeInt(data.length);
                out.write(data);
            }

            if (cartItemsFile.exists()) {
                byte[] data = readAllBytes(cartItemsFile.toPath());
                out.writeUTF("UPDATE_CARTITEMS");
                out.writeUTF(cartItemsFile.getName());
                out.writeInt(data.length);
                out.write(data);
            }

            out.flush();

        } catch (Exception e) {
            System.err.println("Cart sync error: " + e.getMessage());
        }
    }
}

