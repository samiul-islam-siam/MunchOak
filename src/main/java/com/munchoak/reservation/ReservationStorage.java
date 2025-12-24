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

        public ReservationRecord(int resId, String name, String phone, int guests, String date, String time,
                                 String request, String createdAt, String username, int userId) {
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
        }
    }

    public static boolean saveReservation(String name, String phone, int guests, String date, String time, String request) {
        StorageInit.ensureDataDir();
        try {
            int resId = StorageUtil.generateNextIdInFile(StoragePaths.RESERVATIONS_FILE, 1);
            String createdAt = Instant.now().toString();

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATIONS_FILE, true))) {
                dos.writeInt(resId);
                dos.writeUTF(name);
                dos.writeUTF(phone);
                dos.writeInt(guests);
                dos.writeUTF(date);
                dos.writeUTF(time);
                dos.writeUTF(request);
                dos.writeUTF(createdAt);
                dos.writeUTF(Session.getCurrentUsername());
                dos.writeInt(Session.getCurrentUserId());
            }

            setReservationStatus(resId, "Pending");
            return true;
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return false;
        }
    }

    public static List<ReservationRecord> loadReservations() {
        StorageInit.ensureDataDir();
        List<ReservationRecord> list = new ArrayList<>();
        if (!StoragePaths.RESERVATIONS_FILE.exists() || StoragePaths.RESERVATIONS_FILE.length() == 0) return list;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.RESERVATIONS_FILE))) {
            while (dis.available() > 0) {
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
                list.add(new ReservationRecord(resId, name, phone, guests, date, time, request, createdAt, username, userId));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return list;
    }

    public static void setReservationStatus(int resId, String status) {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATION_STATUS_FILE, true))) {
            dos.writeInt(resId);
            dos.writeUTF(status);
            dos.writeUTF(Instant.now().toString());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public static String getReservationStatus(int resId) {
        StorageInit.ensureDataDir();
        if (!StoragePaths.RESERVATION_STATUS_FILE.exists() || StoragePaths.RESERVATION_STATUS_FILE.length() == 0) return "Pending";

        String last = "Pending";
        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.RESERVATION_STATUS_FILE))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String status = dis.readUTF();
                dis.readUTF(); // timestamp
                if (id == resId) last = status;
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return last;
    }
}