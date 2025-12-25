package com.munchoak.manager;

import java.io.File;

public final class StoragePaths {
    private StoragePaths() {}

    public static final File DATA_DIR = new File("src/main/resources/com/munchoak/manager/data");

    // Core files
    public static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");
    public static final File USERS_FILE = new File(DATA_DIR, "users.dat");

    // Food Categories
    public static final File CATEGORIES_FILE = new File(DATA_DIR, "categories.dat");

    // Carts
    public static final File CARTS_FILE = new File(DATA_DIR, "carts.dat");
    public static final File CART_ITEMS_FILE = new File(DATA_DIR, "cartitems.dat");

    // Reservation
    public static final File RESERVATIONS_FILE = new File(DATA_DIR, "reservations.dat");
    public static final File RESERVATION_STATUS_FILE = new File(DATA_DIR, "reservation_status.dat");
    public static final File MESSAGES_FILE = new File(DATA_DIR, "messages.dat");

    // Payment
    public static final File PAYMENT_MASTER_FILE = new File(DATA_DIR, "payments.dat");
    public static final File PAYMENTS_FILE = PAYMENT_MASTER_FILE;
    public static final File PAYMENT_ITEMS_FILE = PAYMENT_MASTER_FILE;
    public static final File PAYMENT_BREAKDOWN_FILE = PAYMENT_MASTER_FILE;

    // Coupons
    public static final File COUPON_USAGE_FILE = new File(DATA_DIR, "coupon_usage.dat");
    public static final File COUPONS_FILE = new File(DATA_DIR, "coupons.dat");

    // Active menu file (changes when attaching a different menu)
    private static File MENU_FILE = new File(DATA_DIR, "menu.dat");
    private static final File MENU_POINTER_FILE = new File(DATA_DIR, "menu_pointer.dat");

    public static File getMenuFile() {
        return MENU_FILE;
    }

    public static void setMenuFile(File file) {
        MENU_FILE = file;
    }

    public static File getMenuPointerFile() { return MENU_POINTER_FILE;}
}