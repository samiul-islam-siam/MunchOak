package com.example.manager;

import com.example.munchoak.Cart;
import com.example.munchoak.FoodItems;
import javafx.scene.control.Alert;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");
    private static final File USERS_FILE = new File(DATA_DIR, "users.dat");
    private static final File CATEGORIES_FILE = new File(DATA_DIR, "categories.dat");
    private static final File PAYMENTS_FILE = new File(DATA_DIR, "payments.dat");
    private static final File CARTS_FILE = new File(DATA_DIR, "carts.dat");
    private static final File CART_ITEMS_FILE = new File(DATA_DIR, "cartitems.dat");
    private static final File PAYMENT_ITEMS_FILE = new File(DATA_DIR, "paymentitems.dat");
    private static final File ORDERS_FILE = new File(DATA_DIR, "orders.dat");
    private static final File RESERVATIONS_FILE = new File(DATA_DIR, "reservations.dat");
    private static final File MENU_POINTER_FILE = new File(DATA_DIR, "menu_pointer.dat");
    private static File MENU_FILE = new File(DATA_DIR, "menu.dat");

    static {
        ensureDataDir();
    }

    static {
        ensureDataDir();
        loadAttachedMenu(); // Load admin-attached menu at startup
    }

    // default
    public static void setMenuFile(File file) {
        MENU_FILE = file;
        saveAttachedMenu(file); // persist the attached menu
    }

    public static File getMenuFile() {
        return MENU_FILE;
    }

    private static void saveAttachedMenu(File menuFile) {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(MENU_POINTER_FILE, false))) {
            dos.writeUTF(menuFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAttachedMenu() {
        ensureDataDir();
        if (!MENU_POINTER_FILE.exists() || MENU_POINTER_FILE.length() == 0) return; // <-- check length
        try (DataInputStream dis = new DataInputStream(new FileInputStream(MENU_POINTER_FILE))) {
            String filename = dis.readUTF();
            File file = new File(DATA_DIR, filename);
            if (file.exists()) {
                MENU_FILE = file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ensureDataDir() {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        try {
            if (!USERS_FILE.exists()) USERS_FILE.createNewFile();
            //if (!MENU_FILE.exists()) MENU_FILE.createNewFile();
            if (!CATEGORIES_FILE.exists()) CATEGORIES_FILE.createNewFile();
            if (!PAYMENTS_FILE.exists()) PAYMENTS_FILE.createNewFile();
            if (!CARTS_FILE.exists()) CARTS_FILE.createNewFile();
            if (!CART_ITEMS_FILE.exists()) CART_ITEMS_FILE.createNewFile();
            if (!PAYMENT_ITEMS_FILE.exists()) PAYMENT_ITEMS_FILE.createNewFile();
            if (!ORDERS_FILE.exists()) ORDERS_FILE.createNewFile();
            if (!RESERVATIONS_FILE.exists()) RESERVATIONS_FILE.createNewFile();
            if (!MENU_POINTER_FILE.exists()) MENU_POINTER_FILE.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    // ----------------- MENU / FOOD -----------------
    public static List<FoodItems> loadMenu() {
        ensureDataDir();
        List<FoodItems> list = new ArrayList<>();
        File menuFile = getMenuFile();
        if (!menuFile.exists() || menuFile.length() == 0) {

            return list; // return empty list, UI can show "Empty menu"
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(getMenuFile()))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String name = dis.readUTF();
                String details = dis.readUTF();
                double price = dis.readDouble();
                String calorie = dis.readUTF();
                String imagePath = dis.readUTF();
                String category = dis.readUTF();
                list.add(new FoodItems(id, name, details, price, calorie, imagePath, category));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<Integer, FoodItems> loadFoodMap() {
        Map<Integer, FoodItems> map = new HashMap<>();
        for (FoodItems f : loadMenu()) map.put(f.getId(), f);
        return map;
    }

    public static void appendMenuItem(FoodItems item) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), true))) {
            dos.writeInt(item.getId());
            dos.writeUTF(item.getName());
            dos.writeUTF(item.getDetails());
            dos.writeDouble(item.getPrice());
            dos.writeUTF(item.getCalories());
            dos.writeUTF(item.getImagePath());
            dos.writeUTF(item.getCategory());
        }
    }

    public static void rewriteMenu(List<FoodItems> items) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), false))) {
            for (FoodItems item : items) {
                dos.writeInt(item.getId());
                dos.writeUTF(item.getName());
                dos.writeUTF(item.getDetails());
                dos.writeDouble(item.getPrice());
                dos.writeUTF(item.getCalories());
                dos.writeUTF(item.getImagePath());
                dos.writeUTF(item.getCategory());
            }
        }
    }

    // ----------------- CATEGORIES -----------------
    public static List<String> loadCategories() {
        ensureDataDir();
        List<String> cats = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(CATEGORIES_FILE))) {
            while (dis.available() > 0) {
                cats.add(dis.readUTF());
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cats;
    }

    public static void addCategory(String name) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, true))) {
            dos.writeUTF(name);
        }
    }

    public static void replaceCategory(String oldName, String newName) throws IOException {
        List<String> cats = loadCategories();
        for (int i = 0; i < cats.size(); i++)
            if (cats.get(i).equals(oldName)) cats.set(i, newName);

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, false))) {
            for (String c : cats) dos.writeUTF(c);
        }

        List<FoodItems> menu = loadMenu();
        boolean changed = false;
        for (FoodItems f : menu) {
            if (f.getCategory().equals(oldName)) {
                f.setCategory(newName);
                changed = true;
            }
        }
        if (changed) rewriteMenu(menu);
    }

    public static void deleteCategory(String name) throws IOException {
        List<String> cats = loadCategories();
        cats.removeIf(s -> s.equals(name));
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, false))) {
            for (String c : cats) dos.writeUTF(c);
        }

        List<FoodItems> menu = loadMenu();
        menu.removeIf(f -> f.getCategory().equals(name));
        rewriteMenu(menu);
    }

    // ----------------- USERS -----------------
    public static boolean userExists(String username) {
        ensureDataDir();
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;
        }
        return false;
    }

    public static int getUserId(String username) {
        ensureDataDir();
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) {
                try {
                    return Integer.parseInt(user[3]);
                } catch (Exception ignored) {
                }
            }
        }
        return -1;
    }


    public static void appendUser(String username, String email, String password) throws IOException {
        ensureDataDir();

        // Start from 2025001 for registered users
        int uid = generateNextIdInFile(USERS_FILE, 4);
        // Generate salt and hash
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(password, salt);
        String storedPassword = salt + ":" + hash; // store salt and hash together
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(USERS_FILE, true))) {
            dos.writeUTF(username);
            dos.writeUTF(email);
            dos.writeUTF(storedPassword);
            dos.writeInt(uid);
        }
    }

    public static void ensureDefaultGuestUser() {
        ensureDataDir();
        List<String[]> users = loadUsers();

        boolean guestExists = false;
        for (String[] user : users) {
            if (user[0].equals("guest")) {
                guestExists = true;
                break;
            }
        }

        if (!guestExists) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(USERS_FILE, true))) {
                dos.writeUTF("guest");
                dos.writeUTF("guest@system.local");
                dos.writeUTF("nopass");
                dos.writeInt(2025000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<String[]> loadUsers() {
        ensureDataDir();
        List<String[]> users = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(USERS_FILE))) {
            while (dis.available() > 0) {
                String username = dis.readUTF();
                String email = dis.readUTF();
                String password = dis.readUTF();
                int uid = dis.readInt();
                users.add(new String[]{username, email, password, String.valueOf(uid)});
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ----------------- PAYMENT & CARTS -----------------
    public static int createPaymentAndCart(int userId, Cart cart, Map<Integer, FoodItems> foodMap, String method) throws IOException {
        ensureDataDir();

        int paymentId = generateNextIdInFile(PAYMENTS_FILE, 3001);
        int cartId = generateNextIdInFile(CARTS_FILE, 1);
        int paymentItemIdStart = generateNextIdInFile(PAYMENT_ITEMS_FILE, 1);

        String timestamp = Instant.now().toString();
        try (DataOutputStream pw = new DataOutputStream(new FileOutputStream(PAYMENTS_FILE, true))) {
            pw.writeInt(paymentId);
            pw.writeInt(userId);
            pw.writeDouble(cart.getTotalPrice(foodMap));
            pw.writeUTF(method);
            pw.writeUTF(timestamp);
        }

        try (DataOutputStream cw = new DataOutputStream(new FileOutputStream(CARTS_FILE, true))) {
            cw.writeInt(cartId);
            cw.writeInt(userId);
            cw.writeInt(paymentId);
            cw.writeUTF(timestamp);
        }

        try (DataOutputStream ciw = new DataOutputStream(new FileOutputStream(CART_ITEMS_FILE, true));
             DataOutputStream piw = new DataOutputStream(new FileOutputStream(PAYMENT_ITEMS_FILE, true))) {

            int paymentItemId = paymentItemIdStart;
            for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
                int foodId = e.getKey();
                int qty = e.getValue();
                ciw.writeInt(cartId);
                ciw.writeInt(foodId);
                ciw.writeInt(qty);

                piw.writeInt(paymentItemId);
                piw.writeInt(paymentId);
                piw.writeInt(foodId);
                piw.writeInt(qty);
                paymentItemId++;
            }
        }

        try (DataOutputStream ow = new DataOutputStream(new FileOutputStream(ORDERS_FILE, true))) {
            for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
                ow.writeInt(paymentId);
                ow.writeInt(userId);
                String itemsStr = e.getKey() + "x" + e.getValue() + ";";
                ow.writeUTF(itemsStr);
                ow.writeUTF(timestamp);
            }
        }

        return paymentId;
    }

    public static List<HistoryRecordSimple> loadPaymentHistory() {
        ensureDataDir();
        List<HistoryRecordSimple> list = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(PAYMENTS_FILE))) {
            while (dis.available() > 0) {
                int pid = dis.readInt();
                int uid = dis.readInt();
                double amount = dis.readDouble();
                String method = dis.readUTF();
                String timestamp = dis.readUTF();
                list.add(new HistoryRecordSimple(uid, pid, timestamp, amount, method));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<Integer, Integer> getCartItemsForPayment(int paymentId) {
        ensureDataDir();
        Map<Integer, Integer> items = new HashMap<>();
        int cartId = -1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(CARTS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                int uid = dis.readInt();
                int pid = dis.readInt();
                String ts = dis.readUTF();
                if (pid == paymentId) {
                    cartId = cid;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cartId == -1) return items;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(CART_ITEMS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                int foodId = dis.readInt();
                int qty = dis.readInt();
                if (cid == cartId) items.put(foodId, qty);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    // ----------------- RESERVATIONS -----------------
    public static boolean saveReservation(String name, String phone, int guests, String date, String time, String request) {
        ensureDataDir();
        try {
            int resId = generateNextIdInFile(RESERVATIONS_FILE, 1);
            String createdAt = Instant.now().toString();
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(RESERVATIONS_FILE, true))) {
                dos.writeInt(resId);
                dos.writeUTF(name);
                dos.writeUTF(phone);
                dos.writeInt(guests);
                dos.writeUTF(date);
                dos.writeUTF(time);
                dos.writeUTF(request);
                dos.writeUTF(createdAt);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int generateNextIdInFile(File f, int startIfEmpty) {
        ensureDataDir();
        int lastId = startIfEmpty - 1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            if (f == PAYMENTS_FILE) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // paymentId
                    dis.readInt();          // userId
                    dis.readDouble();       // amount
                    dis.readUTF();          // method
                    dis.readUTF();          // timestamp
                }
            } else if (f == USERS_FILE) {
                while (dis.available() > 0) {
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    lastId = dis.readInt(); // userId
                }
            } else if (f == MENU_FILE) {
                while (dis.available() > 0) {
                    dis.readInt();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readDouble();
                    dis.readDouble();
                    dis.readUTF();
                    dis.readUTF();
                }
            } else if (f == CATEGORIES_FILE) {
                while (dis.available() > 0) dis.readUTF();
            } else if (f == CARTS_FILE) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // cartId
                    dis.readInt();
                    dis.readInt();
                    dis.readUTF();
                }
            } else if (f == PAYMENT_ITEMS_FILE || f == CART_ITEMS_FILE) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // itemId
                    dis.readInt();
                    dis.readInt();
                }
            } else if (f == RESERVATIONS_FILE) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // resId
                    dis.readUTF();
                    dis.readUTF();
                    dis.readInt();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                }
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastId + 1;
    }


    // ----------------- History Record -----------------
    public static class HistoryRecordSimple {
        public final int userId;
        public final int paymentId;
        public final String timestamp;
        public final double amount;
        public final String paymentMethod;

        public HistoryRecordSimple(int userId, int paymentId, String timestamp, double amount, String paymentMethod) {
            this.userId = userId;
            this.paymentId = paymentId;
            this.timestamp = timestamp;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }
    }


    // ---------------- ADMIN PASSWORD MANAGEMENT ----------------
    private static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");

    static {
        try {
            ensureAdminFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            ensureDataDir();
            ensureAdminFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureAdminFile() throws IOException {
        if (!ADMIN_FILE.exists()) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ADMIN_FILE))) {
                dos.writeUTF("admin123"); // default admin password
            }
        }
    }

    public static boolean verifyAdminPassword(String password) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(ADMIN_FILE))) {
            String saved = dis.readUTF().trim();
            return password.equals(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setAdminPassword(String newPassword) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ADMIN_FILE, false))) {
            dos.writeUTF(newPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- ADMIN USER MANAGEMENT ----------------
    public static List<String[]> readAllUsersSimple() {
        return new ArrayList<>(loadUsers());
    }

    public static int getUserCount() {
        return loadUsers().size();
    }

    // ---------------- USER PASSWORD UTILITIES ----------------
    public static boolean verifyUserPassword(String username, String password) {
        ensureDataDir();
        if (!USERS_FILE.exists()) return false;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(USERS_FILE))) {
            while (dis.available() > 0) {
                String uname = dis.readUTF();
                String email = dis.readUTF();
                //String pwd = dis.readUTF();

                String saltAndHash = dis.readUTF();
                dis.readInt(); // userId, ignored here
                if (uname.equals(username)) {
                    String[] parts = saltAndHash.split(":");
                    if (parts.length != 2) return false;
                    return PasswordUtils.verifyPassword(password, parts[0], parts[1]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateUserPassword(String username, String newPassword) {
        ensureDataDir();
        List<String[]> users = loadUsers(); // load all users
        boolean updated = false;

        for (int i = 0; i < users.size(); i++) {
            String[] u = users.get(i);
            if (u[0].equals(username)) {
                String salt = PasswordUtils.generateSalt();
                String hash = PasswordUtils.hashPassword(newPassword, salt);
                u[2] = salt + ":" + hash; // replace password field

                updated = true;
                break;
            }
        }

        if (updated) {
            // rewrite the USERS_FILE
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(USERS_FILE, false))) {
                for (String[] u : users) {
                    dos.writeUTF(u[0]);  // username
                    dos.writeUTF(u[1]);  // email
                    dos.writeUTF(u[2]);  // salt:hash
                    dos.writeInt(Integer.parseInt(u[3])); // userId
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
