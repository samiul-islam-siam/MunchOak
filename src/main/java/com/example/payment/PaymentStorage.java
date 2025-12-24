package com.example.payment;

import com.example.manager.StorageInit;
import com.example.manager.StoragePaths;
import com.example.manager.StorageUtil;
import com.example.munchoak.Cart;
import com.example.munchoak.FoodItems;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PaymentStorage {
    private PaymentStorage() {}

    public static int createPaymentAndCart(
            int userId,
            Cart cart,
            Map<Integer, FoodItems> foodMap,
            String method,
            double finalTotal
    ) throws IOException {

        StorageInit.ensureDataDir();

        int paymentId = StorageUtil.generateNextIdInFile(StoragePaths.PAYMENTS_FILE, 3001);
        int cartId = StorageUtil.generateNextIdInFile(StoragePaths.CARTS_FILE, 1);
        int paymentItemIdStart = StorageUtil.generateNextIdInFile(StoragePaths.PAYMENT_ITEMS_FILE, 1);

        String timestamp = Instant.now().toString();

        try (DataOutputStream pw = new DataOutputStream(new FileOutputStream(StoragePaths.PAYMENTS_FILE, true))) {
            pw.writeInt(paymentId);
            pw.writeInt(userId);
            pw.writeDouble(finalTotal);
            pw.writeUTF(method);
            pw.writeUTF(timestamp);
        }

        try (DataOutputStream cw = new DataOutputStream(new FileOutputStream(StoragePaths.CARTS_FILE, true))) {
            cw.writeInt(cartId);
            cw.writeInt(userId);
            cw.writeInt(paymentId);
            cw.writeUTF(timestamp);
        }

        try (DataOutputStream ciw = new DataOutputStream(new FileOutputStream(StoragePaths.CART_ITEMS_FILE, true));
             DataOutputStream piw = new DataOutputStream(new FileOutputStream(StoragePaths.PAYMENT_ITEMS_FILE, true))) {

            int paymentItemId = paymentItemIdStart;
            for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
                int foodId = e.getKey();
                int qty = e.getValue();

                ciw.writeInt(cartId);
                ciw.writeInt(foodId);
                ciw.writeInt(qty);

                piw.writeInt(paymentItemId);
                piw.writeInt(paymentId);
                piw.writeInt(foodId);
                piw.writeInt(qty);
                paymentItemId++;
            }
        }

        return paymentId;
    }

    public static int getLastPaymentId() {
        return StorageUtil.generateNextIdInFile(StoragePaths.PAYMENTS_FILE, 3001) - 1;
    }

    public static List<History.HistoryRecord> loadPaymentHistory() {
        StorageInit.ensureDataDir();
        List<History.HistoryRecord> list = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.PAYMENTS_FILE))) {
            while (dis.available() > 0) {
                int pid = dis.readInt();
                int uid = dis.readInt();
                double amount = dis.readDouble();
                String method = dis.readUTF();
                String timestamp = dis.readUTF();
                String status = dis.readUTF();
                list.add(new History.HistoryRecord(uid, pid, timestamp, amount, status, method));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return list;
    }

    public static Map<Integer, Integer> getCartItemsForPayment(int paymentId) {
        StorageInit.ensureDataDir();
        Map<Integer, Integer> items = new HashMap<>();
        int cartId = -1;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.CARTS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                dis.readInt(); // userId
                int pid = dis.readInt();
                dis.readUTF(); // timestamp
                if (pid == paymentId) {
                    cartId = cid;
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        if (cartId == -1) return items;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.CART_ITEMS_FILE))) {
            while (dis.available() > 0) {
                int cid = dis.readInt();
                int foodId = dis.readInt();
                int qty = dis.readInt();
                if (cid == cartId) items.put(foodId, qty);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return items;
    }

    public static PaymentBreakdown getPaymentBreakdown(int paymentId) {
        StorageInit.ensureDataDir();
        if (!StoragePaths.PAYMENT_BREAKDOWN_FILE.exists()) return null;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.PAYMENT_BREAKDOWN_FILE))) {
            while (dis.available() > 0) {
                int pid = dis.readInt();
                double baseSubtotal = dis.readDouble();
                double addons = dis.readDouble();
                double discountAmount = dis.readDouble();
                double tip = dis.readDouble();
                double delivery = dis.readDouble();
                double tax = dis.readDouble();
                double service = dis.readDouble();
                double total = dis.readDouble();
                int userId = dis.readInt();
                String userName = dis.readUTF();

                if (pid == paymentId) {
                    return new PaymentBreakdown(
                            baseSubtotal, addons, discountAmount, tip, delivery, tax, service, total, userId, userName
                    );
                }
            }
        } catch (IOException ignored) {}

        return null;
    }

    public static void savePaymentBreakdown(
            int paymentId,
            double baseSubtotal,
            double addons,
            double discountAmount,
            double tip,
            double delivery,
            double tax,
            double service,
            double total,
            int userId,
            String userName
    ) throws IOException {

        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.PAYMENT_BREAKDOWN_FILE, true))) {
            dos.writeInt(paymentId);
            dos.writeDouble(baseSubtotal);
            dos.writeDouble(addons);
            dos.writeDouble(discountAmount);
            dos.writeDouble(tip);
            dos.writeDouble(delivery);
            dos.writeDouble(tax);
            dos.writeDouble(service);
            dos.writeDouble(total);
            dos.writeInt(userId);
            dos.writeUTF(userName);
        }
    }
}