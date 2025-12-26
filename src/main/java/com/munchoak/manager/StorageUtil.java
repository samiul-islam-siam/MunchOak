package com.munchoak.manager;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Shared low-level helpers.
 * Keep ONLY generic helpers here, not domain logic.
 */
public final class StorageUtil {
    private StorageUtil() {
    }

    public static int generateNextIdInFile(File f, int startIfEmpty) {
        StorageInit.ensureDataDir();
        int lastId = startIfEmpty - 1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {

            if (f.equals(StoragePaths.USERS_FILE)) {
                while (dis.available() > 0) {
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    lastId = dis.readInt(); // userId
                }
            } else if (f.equals(StoragePaths.getMenuFile())) {
                // NOTE: this must match exact fields written by MenuStorage.
                while (dis.available() > 0) {
                    dis.readInt();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readDouble();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readInt();
                    dis.readUTF();
                    dis.readDouble();
                    dis.readUTF();
                    dis.readDouble();
                }
            } else if (f.equals(StoragePaths.CATEGORIES_FILE)) {
                while (dis.available() > 0) dis.readUTF();
            } else if (f.equals(StoragePaths.CARTS_FILE)) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // cartId
                    dis.readInt();
                    dis.readInt();
                    dis.readUTF();
                }
            } else if (f.equals(StoragePaths.RESERVATIONS_FILE)) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // resId
                    dis.readUTF(); // name
                    dis.readUTF(); // phone
                    dis.readInt(); // guests
                    dis.readUTF(); // date
                    dis.readUTF(); // time
                    dis.readUTF(); // request
                    dis.readUTF(); // createdAt
                    dis.readUTF(); // username
                    dis.readInt(); // userId
                    dis.readUTF(); // status
                    dis.readUTF(); // statusUpdatedAt
                }
            }

        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return lastId + 1;
    }
}