/*
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
/*
    // Set password for admin
    public static void setAdminPassword(String newPass) throws IOException {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(newPass, salt);
        writeText(ADMIN_ID + "," + salt + ":" + hash);
    }
*/





/*












// ---------- Admin ----------

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
        public static boolean updateAdminInfo(String adminId, String newUsername, String newEmail, String newContact) {
            List<String> lines = readLines();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(adminId)) {
                        String passwordPart = parts.length > 4 ? parts[4] : "";
                        bw.write(adminId + "," + newUsername + "," + newEmail + "," + newContact + "," + passwordPart);
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
            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hashPassword(newPassword, salt);
            String passwordPart = salt + ":" + hash;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(adminId)) {
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
        public static boolean updateAdminId(String oldId, String newId) {
            List<String> lines = readLines();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(oldId)) {
                        parts[0] = newId;
                    }
                    bw.write(String.join(",", parts));
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                System.err.println("IOException updating admin ID: " + e.getMessage());
                return false;
            }
        }



    }

*/
package com.example.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFileStorage {

    private static final File DATA_DIR = new File("src/main/resources/com/example/manager/data");
    private static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");
    private static final File USERS_FILE = new File(DATA_DIR, "users.dat");

    public static final String ADMIN_ID = "2104"; // unique admin ID

    static {
        try {
            ensureDataDirAndFiles();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // ---------- Admin ----------
    public static boolean verifyAdminPassword(String id, String pass) throws IOException {
        List<String> lines = readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(id) && parts.length >= 5) {
                String[] saltAndHash = parts[parts.length - 1].split(":");
                return PasswordUtils.verifyPassword(pass, saltAndHash[0], saltAndHash[1]);
            }
        }
        return false;
    }

    public static void setAdminPassword(String newPass) throws IOException {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(newPass, salt);
        String passwordPart = salt + ":" + hash;

        List<String> lines = readLines();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
            boolean updated = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(ADMIN_ID)) {
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

    public static String getAdminPassword() throws IOException {
        List<String> lines = readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(ADMIN_ID) && parts.length >= 5) {
                String[] saltAndHash = parts[parts.length - 1].split(":");
                return saltAndHash[1]; // return hash only
            }
        }
        return null;
    }
    public static boolean updateAdminInfo(String newUsername, String newEmail, String newContact) {
        List<String> lines = readLines();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(ADMIN_ID) && parts.length >= 5) {
                    String oldUsername = parts[1];
                    String oldEmail = parts[2];
                    String oldContact = parts[3];
                    String passwordPart = parts[4];

                    if (newUsername == null || newUsername.isEmpty()) newUsername = oldUsername;
                    if (newEmail == null || newEmail.isEmpty()) newEmail = oldEmail;
                    if (newContact == null || newContact.isEmpty()) newContact = oldContact;

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
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(newPassword, salt);
        String passwordPart = salt + ":" + hash;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE, false))) {
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(adminId) && parts.length >= 5) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(ADMIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return lines;
    }

    private static void ensureDataDirAndFiles() throws IOException {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        if (!ADMIN_FILE.exists()) ADMIN_FILE.createNewFile();
        if (!USERS_FILE.exists()) USERS_FILE.createNewFile();
    }
}
