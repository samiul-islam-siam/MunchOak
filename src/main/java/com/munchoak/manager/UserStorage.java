package com.munchoak.manager;

import com.munchoak.authentication.PasswordUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class UserStorage {
    private UserStorage() {
    }

    public static boolean userExists(String username) {
        StorageInit.ensureDataDir();
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;
        }
        return false;
    }

    public static int getUserId(String username) {
        StorageInit.ensureDataDir();
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) {
                try {
                    return Integer.parseInt(user[4]);
                } catch (Exception ignored) {
                }
            }
        }
        return -1;
    }

    public static String getUserEmail(String username) {
        StorageInit.ensureDataDir();
        for (String[] user : loadUsers())
            if (user[0].equals(username)) return user[1];
        return null;
    }

    public static String getUserContact(String username) {
        StorageInit.ensureDataDir();
        for (String[] user : loadUsers())
            if (user[0].equals(username)) return user[2];
        return null;
    }

    public static String getUserPassword(String username) {
        StorageInit.ensureDataDir();
        for (String[] user : loadUsers())
            if (user[0].equals(username)) return user[3];
        return null;
    }

    public static void appendUser(String username, String email, String contactNo, String password) throws IOException {
        StorageInit.ensureDataDir();
        int uid = StorageUtil.generateNextIdInFile(StoragePaths.USERS_FILE, 2025001);

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        String storedPassword = salt + ":" + hash;

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.USERS_FILE, true))) {
            dos.writeUTF(username);
            dos.writeUTF(email);
            dos.writeUTF(contactNo);
            dos.writeUTF(storedPassword);
            dos.writeInt(uid);
        }
    }

    public static List<String[]> loadUsers() {
        StorageInit.ensureDataDir();
        List<String[]> users = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.USERS_FILE))) {
            while (dis.available() > 0) {
                String username = dis.readUTF();
                String email = dis.readUTF();
                String contactNo = dis.readUTF();
                String password = dis.readUTF();
                int uid = dis.readInt();
                users.add(new String[]{username, email, contactNo, password, String.valueOf(uid)});
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return users;
    }

    public static boolean updateUserInfo(int userId, String newUsername, String newEmail, String newContact) {
        List<String[]> allUsers = loadUsers();
        boolean updated = false;

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.USERS_FILE, false))) {
            for (String[] user : allUsers) {
                if (Integer.parseInt(user[4]) == userId) {
                    user[0] = newUsername;
                    user[1] = newEmail;
                    user[2] = newContact;
                    updated = true;
                }
                dos.writeUTF(user[0]);
                dos.writeUTF(user[1]);
                dos.writeUTF(user[2]);
                dos.writeUTF(user[3]);
                dos.writeInt(Integer.parseInt(user[4]));
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return false;
        }

        return updated;
    }
}