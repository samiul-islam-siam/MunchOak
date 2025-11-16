package com.example.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFileStorage {

    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");
    private static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");
    private static final File USER_FILE = new File(DATA_DIR, "users.dat");
    private static final File FOOD_FILE = new File(DATA_DIR, "food.dat");
    private static final File LOG_FILE = new File(DATA_DIR, "activity.log");
    private static final File USERS_FILE = new File("src/main/resources/com/example/manager/data/users.dat");

    public static final String ADMIN_ID = "1"; // unique admin ID

    // Static block to initialize files and default admin password
    static {
        try {
            ensureDataDirAndFiles();
            if (getAdminPasswordHash().isEmpty()) {
                setAdminPassword("admin123"); // default password
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- Admin ----------

    // Verify password for admin
    public static boolean verifyAdminPassword(String pass) throws IOException {
        return verifyAdminPassword(ADMIN_ID, pass);
    }

    public static boolean verifyAdminPassword(String id, String pass) throws IOException {
        List<String> lines = readLines(ADMIN_FILE);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(id)) {
                String[] saltAndHash = parts[1].split(":");
                return PasswordUtils.verifyPassword(pass, saltAndHash[0], saltAndHash[1]);
            }
        }
        return false;
    }

    // Set password for admin
    public static void setAdminPassword(String newPass) throws IOException {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(newPass, salt);
        writeText(ADMIN_FILE, ADMIN_ID + "," + salt + ":" + hash);
    }

    // Get stored hash for admin (used internally)
    private static String getAdminPasswordHash() throws IOException {
        List<String> lines = readLines(ADMIN_FILE);
        if (lines.isEmpty()) return "";
        String[] parts = lines.get(0).split(",");
        return parts.length == 2 ? parts[1] : "";
    }

    // ---------- Users ----------

    public static List<String> getAllUsers() {
        return readLines(USER_FILE);
    }

    public static int countUsers() {
        return getAllUsers().size();
    }

    // ---------- Food Items ----------

    public static List<String> getAllFoodItems() {
        return readLines(FOOD_FILE);
    }

    public static void addFoodItem(String item) throws IOException {
        try (FileWriter fw = new FileWriter(FOOD_FILE, true)) {
            fw.write(item + "\n");
        }
    }

    public static void removeFoodItem(String item) throws IOException {
        List<String> items = getAllFoodItems();
        items.removeIf(i -> i.equalsIgnoreCase(item));
        try (PrintWriter pw = new PrintWriter(FOOD_FILE)) {
            for (String i : items) pw.println(i);
        }
    }

    // ---------- Activity Log ----------

    public static List<String> readLog() {
        List<String> logs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) logs.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    // ---------- Helpers ----------

    private static List<String> readLines(File f) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void writeText(File f, String text) throws IOException {
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(text);
        }
    }

    private static void ensureDataDirAndFiles() throws IOException {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        if (!ADMIN_FILE.exists()) ADMIN_FILE.createNewFile();
        if (!USER_FILE.exists()) USER_FILE.createNewFile();
        if (!FOOD_FILE.exists()) FOOD_FILE.createNewFile();
        if (!LOG_FILE.exists()) LOG_FILE.createNewFile();
    }

    // Load raw users as a list of String arrays [username, email, password, uniqueID]
    public static List<String[]> loadUsersRaw() throws IOException {
        List<String[]> users = new ArrayList<>();
        if (!USERS_FILE.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) { // username,email,password,uniqueID
                    users.add(parts);
                }
            }
        }
        return users;
    }

    // Save raw users list back to file
    public static void saveUsersRaw(List<String[]> users) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String[] user : users) {
                bw.write(String.join(",", user));
                bw.newLine();
            }
        }
    }

    public static boolean updateAdminUniqueID(String username, String newID) throws IOException {
        List<String[]> users = loadUsersRaw(); // load all users as arrays [username, email, password, uniqueID]
        boolean updated = false;

        for (String[] user : users) {
            if (user[0].equals(username)) {
                user[3] = newID; // assuming 4th element is unique ID
                updated = true;
                break;
            }
        }

        if (updated) {
            saveUsersRaw(users); // save back all users
            return true;
        } else {
            return false;
        }
    }
}
