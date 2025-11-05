package com.example.manager;

import com.example.munchoak.Cart;
import com.example.munchoak.FoodItems;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");
    private static final File USERS_FILE = new File(DATA_DIR, "users.dat");
    private static final File MENU_FILE = new File(DATA_DIR, "menu.dat");
    private static final File CATEGORIES_FILE = new File(DATA_DIR, "categories.dat");
    private static final File PAYMENTS_FILE = new File(DATA_DIR, "payments.dat");
    private static final File CARTS_FILE = new File(DATA_DIR, "carts.dat");
    private static final File CART_ITEMS_FILE = new File(DATA_DIR, "cartitems.dat");
    private static final File PAYMENT_ITEMS_FILE = new File(DATA_DIR, "paymentitems.dat");
    private static final File ORDERS_FILE = new File(DATA_DIR, "orders.dat");
    private static final File RESERVATIONS_FILE = new File(DATA_DIR, "reservations.dat");

    static {
        ensureDataDir();
    }

    public static void ensureDataDir() {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        try {
            if (!USERS_FILE.exists()) USERS_FILE.createNewFile();
            if (!MENU_FILE.exists()) MENU_FILE.createNewFile();
            if (!CATEGORIES_FILE.exists()) CATEGORIES_FILE.createNewFile();
            if (!PAYMENTS_FILE.exists()) PAYMENTS_FILE.createNewFile();
            if (!CARTS_FILE.exists()) CARTS_FILE.createNewFile();
            if (!CART_ITEMS_FILE.exists()) CART_ITEMS_FILE.createNewFile();
            if (!PAYMENT_ITEMS_FILE.exists()) PAYMENT_ITEMS_FILE.createNewFile();
            if (!ORDERS_FILE.exists()) ORDERS_FILE.createNewFile();
            if (!RESERVATIONS_FILE.exists()) RESERVATIONS_FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- MENU / FOOD -----------------
    public static List<FoodItems> loadMenu() {
        ensureDataDir();
        List<FoodItems> list = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(MENU_FILE))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String name = dis.readUTF();
                String details = dis.readUTF();
                double price = dis.readDouble();
                double ratings = dis.readDouble();
                String imagePath = dis.readUTF();
                String category = dis.readUTF();
                list.add(new FoodItems(id, name, details, price, ratings, imagePath, category));
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
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(MENU_FILE, true))) {
            dos.writeInt(item.getId());
            dos.writeUTF(item.getName());
            dos.writeUTF(item.getDetails());
            dos.writeDouble(item.getPrice());
            dos.writeDouble(item.getRatings());
            dos.writeUTF(item.getImagePath());
            dos.writeUTF(item.getCategory());
        }
    }

    public static void rewriteMenu(List<FoodItems> items) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(MENU_FILE, false))) {
            for (FoodItems item : items) {
                dos.writeInt(item.getId());
                dos.writeUTF(item.getName());
                dos.writeUTF(item.getDetails());
                dos.writeDouble(item.getPrice());
                dos.writeDouble(item.getRatings());
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
                try { return Integer.parseInt(user[3]); } catch (Exception ignored) {}
            }
        }
        return -1;
    }

    public static void appendUser(String username, String email, String password) throws IOException {
        ensureDataDir();
        int uid = generateNextIdInFile(USERS_FILE, 4, 3, 20250001);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(USERS_FILE, true))) {
            dos.writeUTF(username);
            dos.writeUTF(email);
            dos.writeUTF(password);
            dos.writeInt(uid);
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

        int paymentId = generateNextIdInFile(PAYMENTS_FILE, 1, 0, 3001);
        int cartId = generateNextIdInFile(CARTS_FILE, 1, 0, 1);
        int paymentItemIdStart = generateNextIdInFile(PAYMENT_ITEMS_FILE, 1, 0, 1);

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
        } catch (IOException e) { e.printStackTrace(); }

        if (cartId == -1) return items;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(CART_ITEMS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                int foodId = dis.readInt();
                int qty = dis.readInt();
                if (cid == cartId) items.put(foodId, qty);
            }
        } catch (IOException e) { e.printStackTrace(); }

        return items;
    }

    // ----------------- RESERVATIONS -----------------
    public static boolean saveReservation(String name, String phone, int guests, String date, String time, String request) {
        ensureDataDir();
        try {
            int resId = generateNextIdInFile(RESERVATIONS_FILE, 1, 0, 1);
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

    // ----------------- UTIL -----------------
    private static int generateNextIdInFile(File f, int minColumns, int idColumnIndex, int startIfEmpty) {
        ensureDataDir();
        int lastId = startIfEmpty - 1;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
            while (dis.available() > 0) {
                if (f == USERS_FILE) {
                    dis.readUTF(); dis.readUTF(); dis.readUTF(); lastId = dis.readInt();
                } else if (f == MENU_FILE) {
                    dis.readInt(); dis.readUTF(); dis.readUTF(); dis.readDouble(); dis.readDouble(); dis.readUTF(); dis.readUTF();
                } else if (f == CATEGORIES_FILE) dis.readUTF();
                else if (f == PAYMENTS_FILE) { dis.readInt(); dis.readInt(); dis.readDouble(); dis.readUTF(); dis.readUTF(); lastId = dis.readInt(); }
                else if (f == CARTS_FILE) { dis.readInt(); dis.readInt(); dis.readInt(); dis.readUTF(); lastId = dis.readInt(); }
                else if (f == CART_ITEMS_FILE || f == PAYMENT_ITEMS_FILE) { dis.readInt(); dis.readInt(); dis.readInt(); lastId = dis.readInt(); }
                else if (f == RESERVATIONS_FILE) { dis.readInt(); dis.readUTF(); dis.readUTF(); dis.readInt(); dis.readUTF(); dis.readUTF(); dis.readUTF(); dis.readUTF(); lastId = dis.readInt(); }
                else dis.skipBytes(dis.available());
            }
        } catch (EOFException ignored) {
        } catch (IOException e) { e.printStackTrace(); }
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
}
