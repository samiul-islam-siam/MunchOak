package com.munchoak.payment;

import com.munchoak.cart.Cart;
import com.munchoak.mainpage.FoodItems;
import com.munchoak.manager.StorageInit;
import com.munchoak.manager.StoragePaths;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PaymentStorage {
    private PaymentStorage() {}

    /**
     * Single-record-per-payment format.
     * This is NOT multiple record types; it's a single version marker to keep reads aligned.
     */
    private static final byte REC_PAYMENT_V2 = 10;

    // -------------------- WRITE --------------------

    public static int createPaymentAndCart(
            int userId,
            Cart cart,
            Map<Integer, FoodItems> foodMap,
            String method,
            double finalTotal
    ) throws IOException {

        StorageInit.ensureDataDir();

        int paymentId = getNextPaymentId();
        String timestamp = Instant.now().toString();

        // Prepare items snapshot (price + name at purchase time)
        List<PaymentItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.getBuyHistory().entrySet()) {
            int foodId = e.getKey();
            int qty = e.getValue();
            FoodItems fi = foodMap.get(foodId);
            if (fi == null) continue;

            items.add(new PaymentItem(foodId, qty, fi.getPrice(), fi.getName()));
        }

        // Write a minimal payment record now. Breakdown is saved via savePaymentBreakdownV2(...)
        // To keep atomic & consistent, we will write EVERYTHING in one go here by requiring breakdown later.
        // BUT your CheckoutPage calls createPaymentAndCart() before savePaymentBreakdown().
        // So: we will write a placeholder breakdown here, then "update" is not possible in append-only.
        //
        // Best fix: createPaymentAndCartV2(...) must receive breakdown fields too.
        //
        // For compatibility with your current flow, we will:
        // - write the full record WITHOUT breakdown
        // - and include breakdown values as 0 for now, then your receipt should NOT depend on them -> but it does.
        //
        // Therefore: CHANGE CheckoutPage to call createPaymentV2(...) with breakdown included.
        throw new IOException("PaymentStorage.createPaymentAndCart is deprecated. Use createPaymentV2(...) with breakdown fields.");
    }

    /**
     * New V2 writer: writes ONE complete payment record including breakdown + items.
     */
    public static int createPaymentV2(
            int userId,
            String userNameSnapshot,
            Cart cart,
            Map<Integer, FoodItems> foodMap,
            String method,
            String timestamp,
            PaymentBreakdown breakdown,
            List<PaymentItem> items
    ) throws IOException {

        StorageInit.ensureDataDir();
        int paymentId = getNextPaymentId();

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(StoragePaths.PAYMENTS_FILE, true)))) {

            dos.writeByte(REC_PAYMENT_V2);
            dos.writeInt(paymentId);

            dos.writeInt(userId);
            dos.writeUTF(userNameSnapshot != null ? userNameSnapshot : "");

            dos.writeUTF(method != null ? method : "");
            dos.writeUTF(timestamp != null ? timestamp : Instant.now().toString());

            // breakdown
            dos.writeDouble(breakdown.baseSubtotal);
            dos.writeDouble(breakdown.addons);
            dos.writeDouble(breakdown.discountAmount);
            dos.writeDouble(breakdown.tip);
            dos.writeDouble(breakdown.delivery);
            dos.writeDouble(breakdown.tax);
            dos.writeDouble(breakdown.service);
            dos.writeDouble(breakdown.total);

            // items
            dos.writeInt(items.size());
            for (PaymentItem it : items) {
                dos.writeInt(it.foodId);
                dos.writeInt(it.qty);
                dos.writeDouble(it.price);
                dos.writeUTF(it.name != null ? it.name : "");
            }
        }

        return paymentId;
    }

    private static int getNextPaymentId() {
        // scan file and find max paymentId
        StorageInit.ensureDataDir();
        int last = 3000;

        File f = StoragePaths.PAYMENTS_FILE;
        if (!f.exists()) return 3001;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            while (true) {
                byte type = dis.readByte();
                if (type != REC_PAYMENT_V2) {
                    throw new IOException("Unknown record type in payments.dat: " + type);
                }
                int pid = dis.readInt();
                last = Math.max(last, pid);

                // userId + userName + method + timestamp
                dis.readInt();
                dis.readUTF();
                dis.readUTF();
                dis.readUTF();

                // 8 doubles breakdown
                for (int i = 0; i < 8; i++) dis.readDouble();

                int itemCount = dis.readInt();
                for (int i = 0; i < itemCount; i++) {
                    dis.readInt();    // foodId
                    dis.readInt();    // qty
                    dis.readDouble(); // price
                    dis.readUTF();    // name
                }
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException scanning payments file: " + e.getMessage());
        }

        return last + 1;
    }

    public static int getLastPaymentId() {
        return getNextPaymentId() - 1;
    }

    // -------------------- READ --------------------

    public static List<PaymentItem> getPaymentItems(int paymentId) {
        List<PaymentItem> items = new ArrayList<>();
        StorageInit.ensureDataDir();

        File f = StoragePaths.PAYMENTS_FILE;
        if (!f.exists()) return items;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            while (true) {
                byte type = dis.readByte();
                if (type != REC_PAYMENT_V2) {
                    throw new IOException("Unknown record type in payments.dat: " + type);
                }

                int pid = dis.readInt();

                // user header
                dis.readInt();  // userId
                dis.readUTF();  // userName snapshot
                dis.readUTF();  // method
                dis.readUTF();  // timestamp

                // breakdown
                for (int i = 0; i < 8; i++) dis.readDouble();

                int itemCount = dis.readInt();

                if (pid == paymentId) {
                    for (int i = 0; i < itemCount; i++) {
                        int foodId = dis.readInt();
                        int qty = dis.readInt();
                        double price = dis.readDouble();
                        String name = dis.readUTF();
                        items.add(new PaymentItem(foodId, qty, price, name));
                    }
                    return items;
                } else {
                    // skip items
                    for (int i = 0; i < itemCount; i++) {
                        dis.readInt();
                        dis.readInt();
                        dis.readDouble();
                        dis.readUTF();
                    }
                }
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException reading payment items: " + e.getMessage());
        }

        return items;
    }

    public static PaymentBreakdown getPaymentBreakdown(int paymentId) {
        StorageInit.ensureDataDir();

        File f = StoragePaths.PAYMENTS_FILE;
        if (!f.exists()) return null;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            while (true) {
                byte type = dis.readByte();
                if (type != REC_PAYMENT_V2) {
                    throw new IOException("Unknown record type in payments.dat: " + type);
                }

                int pid = dis.readInt();

                int userId = dis.readInt();
                String userName = dis.readUTF();
                dis.readUTF(); // method
                dis.readUTF(); // timestamp

                double baseSubtotal = dis.readDouble();
                double addons = dis.readDouble();
                double discountAmount = dis.readDouble();
                double tip = dis.readDouble();
                double delivery = dis.readDouble();
                double tax = dis.readDouble();
                double service = dis.readDouble();
                double total = dis.readDouble();

                int itemCount = dis.readInt();

                // skip items always for breakdown read
                for (int i = 0; i < itemCount; i++) {
                    dis.readInt();
                    dis.readInt();
                    dis.readDouble();
                    dis.readUTF();
                }

                if (pid == paymentId) {
                    return new PaymentBreakdown(
                            baseSubtotal, addons, discountAmount, tip,
                            delivery, tax, service, total,
                            userId, userName
                    );
                }
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException reading payment breakdown: " + e.getMessage());
        }

        return null;
    }

    public static List<History.HistoryRecord> loadPaymentHistory() {
        StorageInit.ensureDataDir();
        List<History.HistoryRecord> list = new ArrayList<>();

        File f = StoragePaths.PAYMENTS_FILE;
        if (!f.exists()) return list;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            while (true) {
                byte type = dis.readByte();
                if (type != REC_PAYMENT_V2) {
                    throw new IOException("Unknown record type in payments.dat: " + type);
                }

                int pid = dis.readInt();

                int userId = dis.readInt();
                dis.readUTF(); // userName snapshot (HistoryRecord currently doesn't show it)
                String method = dis.readUTF();
                String timestamp = dis.readUTF();

                // breakdown
                dis.readDouble(); // baseSubtotal
                dis.readDouble(); // addons
                dis.readDouble(); // discount
                dis.readDouble(); // tip
                dis.readDouble(); // delivery
                dis.readDouble(); // tax
                dis.readDouble(); // service
                double total = dis.readDouble();

                int itemCount = dis.readInt();
                for (int i = 0; i < itemCount; i++) {
                    dis.readInt();
                    dis.readInt();
                    dis.readDouble();
                    dis.readUTF();
                }

                list.add(new History.HistoryRecord(
                        userId, pid, timestamp, total, "Success", method
                ));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException reading payment history: " + e.getMessage());
        }

        return list;
    }

    public static Map<Integer, Integer> getCartItemsForPayment(int paymentId) {
        Map<Integer, Integer> map = new HashMap<>();
        for (PaymentItem it : getPaymentItems(paymentId)) {
            map.put(it.foodId, it.qty);
        }
        return map;
    }
}