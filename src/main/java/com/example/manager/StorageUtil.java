package com.example.manager;

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
    private StorageUtil() {}

    public static int generateNextIdInFile(File f, int startIfEmpty) {
        StorageInit.ensureDataDir();
        int lastId = startIfEmpty - 1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {

            if (f.equals(StoragePaths.PAYMENTS_FILE)) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // paymentId
                    dis.readInt();          // userId
                    dis.readDouble();       // amount
                    dis.readUTF();          // method
                    dis.readUTF();          // timestamp
                }
            } else if (f.equals(StoragePaths.USERS_FILE)) {
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
            } else if (f.equals(StoragePaths.PAYMENT_ITEMS_FILE)) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // paymentItemId
                    dis.readInt();          // paymentId
                    dis.readInt();          // foodId
                    dis.readInt();          // qty
                }
            } else if (f.equals(StoragePaths.RESERVATIONS_FILE)) {
                while (dis.available() > 0) {
                    lastId = dis.readInt(); // resId
                    dis.readUTF();
                    dis.readUTF();
                    dis.readInt();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readUTF();
                    dis.readInt();
                }
            }

        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return lastId + 1;
    }
}