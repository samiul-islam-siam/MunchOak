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
    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");      //for directory
    // Data files stored inside DATA_DIR
    private static final File USERS_FILE = new File(DATA_DIR, "users.dat");
    private static final File CATEGORIES_FILE = new File(DATA_DIR, "categories.dat");
    private static final File PAYMENTS_FILE = new File(DATA_DIR, "payments.dat");
    private static final File CARTS_FILE = new File(DATA_DIR, "carts.dat");
    private static final File CART_ITEMS_FILE = new File(DATA_DIR, "cartitems.dat");
    private static final File PAYMENT_ITEMS_FILE = new File(DATA_DIR, "paymentitems.dat");
    private static final File ORDERS_FILE = new File(DATA_DIR, "orders.dat");
    private static final File RESERVATIONS_FILE = new File(DATA_DIR, "reservations.dat");
    private static final File MENU_POINTER_FILE = new File(DATA_DIR, "menu_pointer.dat");
    // Active menu file (changes when attaching a different menu)
    private static File MENU_FILE = new File(DATA_DIR, "menu.dat");

    //Ensuring all necessary files in directory and pointer files
    public static void init() {
        try {
            ensureDataDir();
            loadAttachedMenu();
            ensureAdminFile();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // Updates the current menu file and saves this selection inside the pointer file.
    public static void setMenuFile(File file) {
        MENU_FILE = file;
        saveAttachedMenu(file); // persist the attached menu
    }

    // Returns the currently active menu file
    public static File getMenuFile() {
        return MENU_FILE;
    }

    //Writes the menu filename into the pointer file. This allows switching between multiple menu files.
    private static void saveAttachedMenu(File menuFile) {
        ensureDataDir();
        //when new files of menu is inserted , current menu file is over-write in pointer file
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(MENU_POINTER_FILE, false))) {
            dos.writeUTF(menuFile.getName());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    //Menu pointer file that points multiple files for menu
    //If pointer is empty, default menu.dat is used
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
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public static void ensureDataDir() {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs(); //to create directory
        try {
            if (!USERS_FILE.exists()) USERS_FILE.createNewFile(); //To create category file when it is not created
            if (!CATEGORIES_FILE.exists())      // By default, to have some predefined categories
            {
                String[] initailizedCategories = {
                        "Drinks",
                        "Sweets",
                        "Spicy Foods",
                        "Main Course",
                        "Appetizers"
                };
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, true));
                for (String category : initailizedCategories) {
                    dos.writeUTF(category); // write each category in binary format
                }
            }
            //When there is no such file , create the corresponding one
            if (!PAYMENTS_FILE.exists()) PAYMENTS_FILE.createNewFile();
            if (!CARTS_FILE.exists()) CARTS_FILE.createNewFile();
            if (!CART_ITEMS_FILE.exists()) CART_ITEMS_FILE.createNewFile();
            if (!PAYMENT_ITEMS_FILE.exists()) PAYMENT_ITEMS_FILE.createNewFile();
            if (!ORDERS_FILE.exists()) ORDERS_FILE.createNewFile();
            if (!RESERVATIONS_FILE.exists()) RESERVATIONS_FILE.createNewFile();
            if (!MENU_POINTER_FILE.exists()) MENU_POINTER_FILE.createNewFile();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // ----------------- MENU -----------------
    //Loads all menu items from the currently active menu file. Returns an empty list if file missing or empty.
    public static List<FoodItems> loadMenu() {
        ensureDataDir();
        List<FoodItems> list = new ArrayList<>();   //To store the data in list
        File menuFile = getMenuFile();
        if (!menuFile.exists() || menuFile.length() == 0) {

            return list; // return empty list, UI can show "Empty menu"
        }

        //Reading from current menu file
        try (DataInputStream dis = new DataInputStream(new FileInputStream(getMenuFile()))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String name = dis.readUTF();
                String details = dis.readUTF();
                double price = dis.readDouble();
                String cuisine = dis.readUTF();
                String imagePath = dis.readUTF();
                String category = dis.readUTF();
                list.add(new FoodItems(id, name, details, price, cuisine, imagePath, category));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return list;
    }

    //Food map for showing bills
    public static Map<Integer, FoodItems> loadFoodMap() {
        Map<Integer, FoodItems> map = new HashMap<>();
        for (FoodItems f : loadMenu()) map.put(f.getId(), f);
        return map;
    }

    //To append menu items in current menu file
    public static void appendMenuItem(FoodItems item) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), true))) {
            dos.writeInt(item.getId());
            dos.writeUTF(item.getName());
            dos.writeUTF(item.getDetails());
            dos.writeDouble(item.getPrice());
            dos.writeUTF(item.getCuisine());
            dos.writeUTF(item.getImagePath());
            dos.writeUTF(item.getCategory());
        }
    }

    // rewrite full menu file from in-memory list
    public static void rewriteMenu(List<FoodItems> items) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), false))) {
            for (FoodItems item : items) {
                dos.writeInt(item.getId());
                dos.writeUTF(item.getName());
                dos.writeUTF(item.getDetails());
                dos.writeDouble(item.getPrice());
                dos.writeUTF(item.getCuisine());
                dos.writeUTF(item.getImagePath());
                dos.writeUTF(item.getCategory());
            }
        }
    }

    // ----------------- CATEGORIES -----------------
    //Loads all categories from the category file.
    public static List<String> loadCategories() {
        ensureDataDir();
        List<String> cats = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(CATEGORIES_FILE))) {
            while (dis.available() > 0) {
                cats.add(dis.readUTF());    //To read the categories
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return cats;
    }

    //Adding new category , appending the file
    public static void addCategory(String name) throws IOException {
        ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, true))) {
            dos.writeUTF(name);
        }
    }

    //For renaming category
    public static void replaceCategory(String oldName, String newName) throws IOException {
        List<String> cats = loadCategories();       //Reading categories
        for (int i = 0; i < cats.size(); i++)
            if (cats.get(i).equals(oldName)) cats.set(i, newName);  //Setting new name

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(CATEGORIES_FILE, false))) {
            for (String c : cats) dos.writeUTF(c);
        }

        //Updating menu with renamed-category
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

    //Deleting category and updating menu
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
    //Checking user already exist or not (Used for login)
    public static boolean userExists(String username) {
        ensureDataDir();
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;
        }
        return false;
    }

    //Accessing user id for a particular user (To track current user)
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

    //While Registration is successful new user information will append in user.dat file
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

    //'guest' is default user while no login is occurred
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
        //guest has particular default data-formate
        if (!guestExists) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(USERS_FILE, true))) {
                dos.writeUTF("guest");
                dos.writeUTF("guest@system.local");
                dos.writeUTF("nopass");
                dos.writeInt(2025000);
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }

    //To load all information of users
    public static List<String[]> loadUsers() {
        ensureDataDir();
        List<String[]> users = new ArrayList<>();   //storing in list
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
            System.err.println("IOException: " + e.getMessage());
        }
        return users;
    }

    // ----------------- PAYMENT & CARTS -----------------
    //For payments and carts (useage in checkout)
    /**
     * Creates a payment, cart, and payment-items entry.
     * Returns the generated payment ID.
     */
    public static int createPaymentAndCart(int userId, Cart cart, Map<Integer, FoodItems> foodMap, String method) throws IOException {
        ensureDataDir();

        int paymentId = generateNextIdInFile(PAYMENTS_FILE, 3001);  //First payemnt id starts from 3001
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

        // Save cart record
        try (DataOutputStream cw = new DataOutputStream(new FileOutputStream(CARTS_FILE, true))) {
            cw.writeInt(cartId);
            cw.writeInt(userId);
            cw.writeInt(paymentId);
            cw.writeUTF(timestamp);
        }

        // Save cart items + payment items
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
        return paymentId;
    }


    //Reading history from payments_file
    public static List<HistoryRecordSimple> loadPaymentHistory() {
        ensureDataDir();
        List<HistoryRecordSimple> list = new ArrayList<>(); //storing in a list
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
            System.err.println("IOException: " + e.getMessage());
        }
        return list;
    }

    //Cart_files to show bills
    /**
     * Loads cart items used for a specific payment ID.
     * Returns map: foodId → quantity
     */
    public static Map<Integer, Integer> getCartItemsForPayment(int paymentId) {
        ensureDataDir();
        Map<Integer, Integer> items = new HashMap<>();
        int cartId = -1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(CARTS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                dis.readInt();
                int pid = dis.readInt();
                dis.readUTF();
                if (pid == paymentId) {
                    cartId = cid;
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
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
            System.err.println("IOException: " + e.getMessage());
        }

        return items;
    }

    // ----------------- RESERVATIONS -----------------
    //Making new reservations
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
            System.err.println("IOException: " + e.getMessage());
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
            System.err.println("IOException: " + e.getMessage());
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

    private static void ensureAdminFile() throws IOException {
        if (!ADMIN_FILE.exists()) {
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(ADMIN_FILE))) {
                dos.writeUTF("admin123"); // default admin password
            }
        }
    }

    // ---------------- USER PASSWORD UTILITIES ----------------
    public static boolean verifyUserPassword(String username, String password) {
        ensureDataDir();
        if (!USERS_FILE.exists()) return false;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(USERS_FILE))) {
            while (dis.available() > 0) {
                String uname = dis.readUTF();
                dis.readUTF();  //email ignored here
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
            System.err.println("IOException: " + e.getMessage());
        }
        return false;
    }

    public static void updateUserPassword(String username, String newPassword) {
        ensureDataDir();
        List<String[]> users = loadUsers(); // load all users
        boolean updated = false;

        for (String[] u : users) {
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
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }
}

