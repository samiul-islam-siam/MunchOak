package com.example.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFileStorage {

    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");
    private static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");
    private static final File USERS_FILE = new File(DATA_DIR, "users.dat");

    public static final String ADMIN_ID = "2104"; // unique admin ID

    // Static block to initialize files
    static {
        try {
            ensureDataDirAndFiles();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // ---------- Admin ----------
    // Verify password for admin
    public static boolean verifyAdminPassword(String id, String pass) throws IOException {
        List<String> lines = readLines();
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
        writeText(ADMIN_ID + "," + salt + ":" + hash);
    }

    // ---------- Helpers ----------
    private static List<String> readLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AdminFileStorage.ADMIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return lines;
    }

    // AdminFileStorage.java
    public static String getAdminPassword() throws IOException {
        List<String> lines = readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(ADMIN_ID)) {
                String[] saltAndHash = parts[1].split(":");
                return saltAndHash[1]; // return hash only (if needed)
            }
        }
        return null;
    }

    private static void writeText(String text) throws IOException {
        try (FileWriter fw = new FileWriter(AdminFileStorage.ADMIN_FILE)) {
            fw.write(text);
        }
    }

    private static void ensureDataDirAndFiles() throws IOException {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        if (!ADMIN_FILE.exists()) ADMIN_FILE.createNewFile();
        if (!USERS_FILE.exists()) USERS_FILE.createNewFile();
    }
}
