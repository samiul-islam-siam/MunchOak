package com.munchoak.manager;

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

            // reservation (single file only)
            if (!StoragePaths.RESERVATIONS_FILE.exists()) StoragePaths.RESERVATIONS_FILE.createNewFile();

            if (!StoragePaths.getMenuPointerFile().exists()) StoragePaths.getMenuPointerFile().createNewFile();

            // notifications (renamed from messages.dat)
            if (!StoragePaths.NOTIFICATIONS_FILE.exists()) StoragePaths.NOTIFICATIONS_FILE.createNewFile();

            // payment (single file)
            if (!StoragePaths.PAYMENTS_FILE.exists()) StoragePaths.PAYMENTS_FILE.createNewFile();

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