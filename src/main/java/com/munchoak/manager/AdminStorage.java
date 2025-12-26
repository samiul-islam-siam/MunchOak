package com.munchoak.manager;

import com.munchoak.authentication.PasswordUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages admin credentials/info in admin.dat.
 * Format (single line preferred):
 *   ADMIN_ID,username,email,contact,salt:hash
 *
 * Rules:
 * - ADMIN_ID is constant and cannot be changed.
 * - Admin username is constant once created (cannot be changed via update methods).
 * - Only email/contact/password are updatable.
 */
public final class AdminStorage {

    public static final String ADMIN_ID = "2104";

    private AdminStorage() {}

    // ---------- Public API ----------

    public static boolean verifyAdminPassword(String id, String pass) throws IOException {
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        for (String line : readLines()) {
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
     * Preserves existing username/email/contact if present.
     */
    public static void setAdminPassword(String newPass) throws IOException {
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        String passwordPart = hashPasswordPart(newPass);
        List<String> lines = readLines();

        boolean updated = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts.length >= 1 && parts[0].equals(ADMIN_ID)) {
                    String username = (parts.length > 1 && !parts[1].isBlank()) ? parts[1] : "admin";
                    String email = (parts.length > 2 && !parts[2].isBlank()) ? parts[2] : "admin@munchoak.com";
                    String contact = (parts.length > 3 && !parts[3].isBlank()) ? parts[3] : "N/A";

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
     * Returns ONLY hash part (backward compatible with your existing Session usage).
     * Note: this is not a password; it's the hash (without salt).
     */
    public static String getAdminPassword() throws IOException {
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        for (String line : readLines()) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[0].equals(ADMIN_ID)) {
                String[] saltAndHash = parts[parts.length - 1].split(":");
                if (saltAndHash.length != 2) return null;
                return saltAndHash[1];
            }
        }
        return null;
    }

    /**
     * ✅ Safe method: updates ONLY email & contact for the constant ADMIN_ID.
     * Username and ADMIN_ID cannot be changed here.
     */
    public static boolean updateAdminContactAndEmail(String newEmail, String newContact) {
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        List<String> lines = readLines();
        boolean found = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts.length >= 5 && parts[0].equals(ADMIN_ID)) {
                    found = true;

                    String username = parts[1]; // locked
                    String email = parts[2];
                    String contact = parts[3];
                    String passwordPart = parts[4];

                    String finalEmail = (newEmail == null || newEmail.isBlank()) ? email : newEmail;
                    String finalContact = (newContact == null || newContact.isBlank()) ? contact : newContact;

                    // enforce constant ADMIN_ID + constant username
                    bw.write(ADMIN_ID + "," + username + "," + finalEmail + "," + finalContact + "," + passwordPart);
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            // If admin line is missing, create it with locked username "admin"
            if (!found) {
                bw.write(ADMIN_ID + ",admin," +
                        (newEmail == null || newEmail.isBlank() ? "admin@munchoak.com" : newEmail) + "," +
                        (newContact == null || newContact.isBlank() ? "N/A" : newContact) + "," +
                        hashPasswordPart("Admin@123")); // default only if missing; change if you want
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("IOException updating admin info: " + e.getMessage());
            return false;
        }
    }

    /**
     * Backward compatible method (old signature).
     * ✅ Ignores newUsername completely to enforce "admin cannot change username".
     */
    public static boolean updateAdminInfo(String newUsernameIgnored, String newEmail, String newContact) {
        return updateAdminContactAndEmail(newEmail, newContact);
    }

    /**
     * Updates admin password ONLY for ADMIN_ID.
     * If someone passes another adminId, it will still only update if it equals ADMIN_ID.
     */
    public static boolean updateAdminPassword(String adminId, String newPassword) {
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        if (adminId == null || !ADMIN_ID.equals(adminId)) {
            return false; // enforce constant admin id
        }

        List<String> lines = readLines();
        String passwordPart = hashPasswordPart(newPassword);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(StoragePaths.ADMIN_FILE, false))) {
            boolean updated = false;

            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts.length >= 5 && parts[0].equals(ADMIN_ID)) {
                    // keep username/email/contact as-is
                    bw.write(ADMIN_ID + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + passwordPart);
                    updated = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            if (!updated) {
                // if missing, create admin line
                bw.write(ADMIN_ID + ",admin,admin@munchoak.com,N/A," + passwordPart);
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
        StorageInit.ensureDataDir();
        ensureAdminFileExists();

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(StoragePaths.ADMIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return lines;
    }

    private static void ensureAdminFileExists() {
        try {
            if (!StoragePaths.ADMIN_FILE.exists()) {
                StoragePaths.ADMIN_FILE.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("IOException creating admin file: " + e.getMessage());
        }
    }

    private static String hashPasswordPart(String newPass) {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(newPass, salt);
        return salt + ":" + hash;
    }
}