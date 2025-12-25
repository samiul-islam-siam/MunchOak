package com.munchoak.manager;

import com.munchoak.authentication.PasswordUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages admin credentials/info in admin.dat.
 * Format (single line preferred):
 *   ADMIN_ID,username,email,contact,salt:hash
 */
public final class AdminStorage {

    public static final String ADMIN_ID = "2104"; // unique admin ID

    private AdminStorage() {}

    // ---------- Admin ----------
    public static boolean verifyAdminPassword(String id, String pass) throws IOException {
        List<String> lines = readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[0].equals(id)) {
                String[] saltAndHash = parts[parts.length - 1].split(":");
                if (saltAndHash.length != 2) return false;
                return PasswordUtil.verifyPassword(pass, saltAndHash[0], saltAndHash[1]);
            }
        }
        return false;
    }

    /**
     * Sets password for ADMIN_ID. Creates admin line if missing.
     */
    public static void setAdminPassword(String newPass) throws IOException {
        String passwordPart = hashPasswordPart(newPass);

        List<String> lines = readLines();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            boolean updated = false;

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(ADMIN_ID)) {
                    String username = parts.length > 1 ? parts[1] : "admin";
                    String email = parts.length > 2 ? parts[2] : "admin@munchoak.com";
                    String contact = parts.length > 3 ? parts[3] : "N/A";
                    bw.write(ADMIN_ID + "," + username + "," + email + "," + contact + "," + passwordPart);
                    updated = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            if (!updated) {
                bw.write(ADMIN_ID + ",admin,admin@munchoak.com,N/A," + passwordPart);
                bw.newLine();
            }
        }
    }

    /**
     * Returns hash only (as your existing method does). Consider returning full salt:hash instead.
     */
    public static String getAdminPassword() throws IOException {
        List<String> lines = readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[0].equals(ADMIN_ID)) {
                String[] saltAndHash = parts[parts.length - 1].split(":");
                if (saltAndHash.length != 2) return null;
                return saltAndHash[1];
            }
        }
        return null;
    }

    public static boolean updateAdminInfo(String newUsername, String newEmail, String newContact) {
        List<String> lines = readLines();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(ADMIN_ID)) {
                    String username = parts[1];
                    String email = parts[2];
                    String contact = parts[3];
                    String passwordPart = parts[4];

                    if (newUsername == null || newUsername.isEmpty()) newUsername = username;
                    if (newEmail == null || newEmail.isEmpty()) newEmail = email;
                    if (newContact == null || newContact.isEmpty()) newContact = contact;

                    bw.write(ADMIN_ID + "," + newUsername + "," + newEmail + "," + newContact + "," + passwordPart);
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("IOException updating admin info: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateAdminPassword(String adminId, String newPassword) {
        List<String> lines = readLines();
        String passwordPart = hashPasswordPart(newPassword);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(adminId)) {
                    bw.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + passwordPart);
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("IOException updating admin password: " + e.getMessage());
            return false;
        }
    }

    // ---------- Helpers ----------
    public static List<String> readLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(StoragePaths.ADMIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return lines;
    }

    private static String hashPasswordPart(String newPass) {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(newPass, salt);
        return salt + ":" + hash;
    }
}