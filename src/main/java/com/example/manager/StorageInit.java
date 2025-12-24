package com.example.manager;

import java.io.File;
import java.io.IOException;

public final class StorageInit {
    private StorageInit() {}

    public static void init() {
        try {
            ensureDataDir();
            MenuStorage.loadAttachedMenu();
            ensureAdminFile();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public static void ensureDataDir() {
        if (!StoragePaths.DATA_DIR.exists()) StoragePaths.DATA_DIR.mkdirs();

        try {
            // coupons
            if (!StoragePaths.COUPONS_FILE.exists()) StoragePaths.COUPONS_FILE.createNewFile();
            if (!StoragePaths.COUPON_USAGE_FILE.exists()) StoragePaths.COUPON_USAGE_FILE.createNewFile();

            // primary files
            if (!StoragePaths.USERS_FILE.exists()) StoragePaths.USERS_FILE.createNewFile();
            if (!StoragePaths.CATEGORIES_FILE.exists()) StoragePaths.CATEGORIES_FILE.createNewFile();
            if (!StoragePaths.CARTS_FILE.exists()) StoragePaths.CARTS_FILE.createNewFile();
            if (!StoragePaths.CART_ITEMS_FILE.exists()) StoragePaths.CART_ITEMS_FILE.createNewFile();
            if (!StoragePaths.RESERVATIONS_FILE.exists()) StoragePaths.RESERVATIONS_FILE.createNewFile();
            if (!StoragePaths.MENU_POINTER_FILE.exists()) StoragePaths.MENU_POINTER_FILE.createNewFile();
            if (!StoragePaths.MESSAGES_FILE.exists()) StoragePaths.MESSAGES_FILE.createNewFile();
            if (!StoragePaths.RESERVATION_STATUS_FILE.exists()) StoragePaths.RESERVATION_STATUS_FILE.createNewFile();
            if(!StoragePaths.PAYMENT_MASTER_FILE.exists()) StoragePaths.PAYMENT_MASTER_FILE.createNewFile();

            // seed categories if empty/new
            CategoryStorage.ensureSeedCategories();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    private static void ensureAdminFile() throws IOException {
        File admin = StoragePaths.ADMIN_FILE;
        if (!admin.exists()) {
            StoragePaths.ADMIN_FILE.createNewFile();
        }
    }
}