package com.munchoak.coupon;

import com.munchoak.manager.StorageInit;
import com.munchoak.manager.StoragePaths;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CouponStorage {
    private CouponStorage() {}

    public static class Coupon {
        public final String code;
        public final double discount;
        public final String expiry;
        public final int usageLimit;
        public final int usedCount;

        public Coupon(String code, double discount, String expiry, int usageLimit, int usedCount) {
            this.code = code;
            this.discount = discount;
            this.expiry = expiry;
            this.usageLimit = usageLimit;
            this.usedCount = usedCount;
        }
    }

    public static void addCoupon(String code, double discount, String expiry, int usageLimit) throws IOException {
        try {
            LocalDate expiryDate = LocalDate.parse(expiry);
            if (!expiryDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Expiry date must be in the future!");
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid expiry format! Must be YYYY-MM-DD");
        }

        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.COUPONS_FILE, true))) {
            dos.writeUTF(code);
            dos.writeDouble(discount);
            dos.writeUTF(expiry);
            dos.writeInt(usageLimit);
            dos.writeInt(0);
        }
    }

    public static List<Coupon> loadCoupons() {
        StorageInit.ensureDataDir();
        List<Coupon> coupons = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.COUPONS_FILE))) {
            while (dis.available() > 0) {
                String code = dis.readUTF();
                double discount = dis.readDouble();
                String expiry = dis.readUTF();
                int usageLimit = dis.readInt();
                int usedCount = dis.readInt();
                coupons.add(new Coupon(code, discount, expiry, usageLimit, usedCount));
            }
        } catch (IOException ignored) {}
        return coupons;
    }

    public static void editCoupon(String code, Double newDiscount, String newExpiry, Integer newUsageLimit) throws IOException {
        List<Coupon> coupons = loadCoupons();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.COUPONS_FILE, false))) {
            for (Coupon c : coupons) {
                Coupon updated = c;
                if (c.code.equals(code)) {
                    double discount = (newDiscount != null) ? newDiscount : c.discount;
                    String expiry = (newExpiry != null && !newExpiry.isEmpty()) ? newExpiry : c.expiry;
                    int usageLimit = (newUsageLimit != null) ? newUsageLimit : c.usageLimit;
                    updated = new Coupon(code, discount, expiry, usageLimit, c.usedCount);
                }

                dos.writeUTF(updated.code);
                dos.writeDouble(updated.discount);
                dos.writeUTF(updated.expiry);
                dos.writeInt(updated.usageLimit);
                dos.writeInt(updated.usedCount);
            }
        }
    }

    public static boolean deleteCoupon(String codeToDelete) {
        try {
            List<Coupon> coupons = loadCoupons();
            boolean existed = false;

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.COUPONS_FILE, false))) {
                for (Coupon c : coupons) {
                    if (c.code.equals(codeToDelete)) {
                        existed = true;
                        continue;
                    }
                    dos.writeUTF(c.code);
                    dos.writeDouble(c.discount);
                    dos.writeUTF(c.expiry);
                    dos.writeInt(c.usageLimit);
                    dos.writeInt(c.usedCount);
                }
            }
            return existed;
        } catch (IOException e) {
            System.err.println("deleteCoupon IOException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return codes:
     * 0 valid
     * 1 already used this coupon
     * 2 user already used a different coupon
     * 3 expired
     * 4 usage limit reached
     * 5 invalid code
     * -1 error
     */
    public static int validateCoupon(String code, int userId) {
        try {
            List<Coupon> coupons = loadCoupons();

            if (StoragePaths.COUPON_USAGE_FILE.exists()) {
                try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.COUPON_USAGE_FILE))) {
                    while (dis.available() > 0) {
                        String usedCode = dis.readUTF();
                        int uid = dis.readInt();
                        if (uid == userId) return usedCode.equals(code) ? 1 : 2;
                    }
                }
            }

            for (Coupon c : coupons) {
                if (c.code.equals(code)) {
                    LocalDate expiryDate = LocalDate.parse(c.expiry);
                    if (!expiryDate.isAfter(LocalDate.now())) return 3;
                    if (c.usedCount >= c.usageLimit) return 4;
                    return 0;
                }
            }
            return 5;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void consumeCoupon(String code, int userId) throws IOException {
        List<Coupon> coupons = loadCoupons();

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.COUPONS_FILE, false));
             DataOutputStream usageOut = new DataOutputStream(new FileOutputStream(StoragePaths.COUPON_USAGE_FILE, true))) {

            for (Coupon c : coupons) {
                Coupon updated = c;
                if (c.code.equals(code)) {
                    updated = new Coupon(c.code, c.discount, c.expiry, c.usageLimit, c.usedCount + 1);
                    usageOut.writeUTF(code);
                    usageOut.writeInt(userId);
                }

                dos.writeUTF(updated.code);
                dos.writeDouble(updated.discount);
                dos.writeUTF(updated.expiry);
                dos.writeInt(updated.usageLimit);
                dos.writeInt(updated.usedCount);
            }
        }
    }
}