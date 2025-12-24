package com.example.manager;

import java.io.File;

public final class StoragePaths {
    private StoragePaths() {}

    public static final File DATA_DIR =
            new File("src/main/resources/com/example/manager/data");

    // Core files
    public static final File USERS_FILE = new File(DATA_DIR, "users.dat");
    public static final File CATEGORIES_FILE = new File(DATA_DIR, "categories.dat");
    public static final File PAYMENTS_FILE = new File(DATA_DIR, "payments.dat");
    public static final File CARTS_FILE = new File(DATA_DIR, "carts.dat");
    public static final File CART_ITEMS_FILE = new File(DATA_DIR, "cartitems.dat");
    public static final File PAYMENT_ITEMS_FILE = new File(DATA_DIR, "paymentitems.dat");
    public static final File ORDERS_FILE = new File(DATA_DIR, "orders.dat");
    public static final File RESERVATIONS_FILE = new File(DATA_DIR, "reservations.dat");
    public static final File MENU_POINTER_FILE = new File(DATA_DIR, "menu_pointer.dat");
    public static final File PAYMENT_DISCOUNTS_FILE = new File(DATA_DIR, "payment_discounts.dat");
    public static final File MESSAGES_FILE = new File(DATA_DIR, "messages.dat");
    public static final File PAYMENT_BREAKDOWN_FILE = new File(DATA_DIR, "payment_breakdown.dat");
    public static final File ADMIN_FILE = new File(DATA_DIR, "admin.dat");

    // Reservation status
    public static final File RESERVATION_STATUS_FILE =
            new File(DATA_DIR, "reservation_status.dat");

    // Coupons
    public static final File COUPON_USAGE_FILE = new File(DATA_DIR, "coupon_usage.dat");
    public static final File COUPONS_FILE = new File(DATA_DIR, "coupons.dat");

    // Active menu file (changes when attaching a different menu)
    private static File MENU_FILE = new File(DATA_DIR, "menu.dat");

    public static File getMenuFile() {
        return MENU_FILE;
    }

    public static void setMenuFile(File file) {
        MENU_FILE = file;
    }
}