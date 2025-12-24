package com.munchoak.authentication;

import com.munchoak.manager.StorageInit;
import com.munchoak.manager.StoragePaths;
import com.munchoak.manager.UserStorage;

import java.io.*;
import java.util.List;

public final class PasswordStorage {
    private PasswordStorage() {}

    public static boolean verifyUserPassword(String username, String password) {
        StorageInit.ensureDataDir();
        if (!StoragePaths.USERS_FILE.exists()) return false;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.USERS_FILE))) {
            while (dis.available() > 0) {
                String uname = dis.readUTF();
                dis.readUTF();  // email
                dis.readUTF();  // contact
                String saltAndHash = dis.readUTF();
                dis.readInt();  // userId

                if (uname.equals(username)) {
                    String[] parts = saltAndHash.split(":");
                    if (parts.length != 2) return false;
                    return PasswordUtil.verifyPassword(password, parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return false;
    }

    public static void updateUserPassword(String username, String newPassword) {
        StorageInit.ensureDataDir();
        List<String[]> users = UserStorage.loadUsers();
        boolean updated = false;

        for (String[] u : users) {
            if (u[0].equals(username)) {
                String salt = PasswordUtil.generateSalt();
                String hash = PasswordUtil.hashPassword(newPassword, salt);
                u[3] = salt + ":" + hash;
                updated = true;
                break;
            }
        }

        if (!updated) return;

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.USERS_FILE, false))) {
            for (String[] u : users) {
                dos.writeUTF(u[0]);
                dos.writeUTF(u[1]);
                dos.writeUTF(u[2]);
                dos.writeUTF(u[3]);
                dos.writeInt(Integer.parseInt(u[4]));
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}