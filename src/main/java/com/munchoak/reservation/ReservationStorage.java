package com.munchoak.reservation;

import com.munchoak.manager.Session;
import com.munchoak.manager.StorageInit;
import com.munchoak.manager.StoragePaths;
import com.munchoak.manager.StorageUtil;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class ReservationStorage {
    private ReservationStorage() {}

    public static class ReservationRecord {
        public final int resId;
        public final String name;
        public final String phone;
        public final int guests;
        public final String date;
        public final String time;
        public final String request;
        public final String createdAt;
        public final String username;
        public final int userId;

        public final String status;          // NEW
        public final String statusUpdatedAt; // NEW

        public ReservationRecord(int resId, String name, String phone, int guests, String date, String time,
                                 String request, String createdAt, String username, int userId,
                                 String status, String statusUpdatedAt) {
            this.resId = resId;
            this.name = name;
            this.phone = phone;
            this.guests = guests;
            this.date = date;
            this.time = time;
            this.request = request;
            this.createdAt = createdAt;
            this.username = username;
            this.userId = userId;
            this.status = status;
            this.statusUpdatedAt = statusUpdatedAt;
        }
    }

    public static boolean saveReservation(String name, String phone, int guests, String date, String time, String request) {
        StorageInit.ensureDataDir();
        try {
            int resId = StorageUtil.generateNextIdInFile(StoragePaths.RESERVATIONS_FILE, 1);
            String createdAt = Instant.now().toString();

            ReservationRecord rec = new ReservationRecord(
                    resId, name, phone, guests, date, time, request,
                    createdAt, Session.getCurrentUsername(), Session.getCurrentUserId(),
                    "Pending", createdAt
            );

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATIONS_FILE, true))) {
                write(dos, rec);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Reservation save error: " + e.getMessage());
            return false;
        }
    }

    public static List<ReservationRecord> loadReservations() {
        StorageInit.ensureDataDir();
        List<ReservationRecord> list = new ArrayList<>();
        if (!StoragePaths.RESERVATIONS_FILE.exists() || StoragePaths.RESERVATIONS_FILE.length() == 0) return list;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.RESERVATIONS_FILE))) {
            while (dis.available() > 0) list.add(read(dis));
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("Reservation load error: " + e.getMessage());
        }
        return list;
    }

    public static boolean setReservationStatus(int resId, String status) {
        StorageInit.ensureDataDir();
        List<ReservationRecord> all = loadReservations();

        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            ReservationRecord r = all.get(i);
            if (r.resId != resId) continue;

            found = true;

            String current = r.status == null ? "Pending" : r.status;

            // finalized means: cannot switch to the other decision
            if (!"Pending".equals(current)) {
                if (!current.equals(status)) return false; // only idempotent allowed
                return true;
            }

            all.set(i, new ReservationRecord(
                    r.resId, r.name, r.phone, r.guests, r.date, r.time, r.request,
                    r.createdAt, r.username, r.userId,
                    status, Instant.now().toString()
            ));
            break;
        }

        if (!found) return false;

        return rewriteAll(all);
    }

    public static String getReservationStatus(int resId) {
        for (ReservationRecord r : loadReservations()) {
            if (r.resId == resId) return r.status == null ? "Pending" : r.status;
        }
        return "Pending";
    }

    private static boolean rewriteAll(List<ReservationRecord> list) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATIONS_FILE, false))) {
            for (ReservationRecord r : list) write(dos, r);
            return true;
        } catch (IOException e) {
            System.err.println("Reservation rewrite error: " + e.getMessage());
            return false;
        }
    }

    private static void write(DataOutputStream dos, ReservationRecord r) throws IOException {
        dos.writeInt(r.resId);
        dos.writeUTF(r.name);
        dos.writeUTF(r.phone);
        dos.writeInt(r.guests);
        dos.writeUTF(r.date);
        dos.writeUTF(r.time);
        dos.writeUTF(r.request);
        dos.writeUTF(r.createdAt);
        dos.writeUTF(r.username);
        dos.writeInt(r.userId);
        dos.writeUTF(r.status == null ? "Pending" : r.status);
        dos.writeUTF(r.statusUpdatedAt == null ? r.createdAt : r.statusUpdatedAt);
    }

    private static ReservationRecord read(DataInputStream dis) throws IOException {
        int resId = dis.readInt();
        String name = dis.readUTF();
        String phone = dis.readUTF();
        int guests = dis.readInt();
        String date = dis.readUTF();
        String time = dis.readUTF();
        String request = dis.readUTF();
        String createdAt = dis.readUTF();
        String username = dis.readUTF();
        int userId = dis.readInt();
        String status = dis.readUTF();
        String statusUpdatedAt = dis.readUTF();

        return new ReservationRecord(resId, name, phone, guests, date, time, request,
                createdAt, username, userId, status, statusUpdatedAt);
    }
}