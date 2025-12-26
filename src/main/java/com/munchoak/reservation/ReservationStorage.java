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
    private ReservationStorage() {
    }

    /**
     * NEW reservations.dat format (single file, latest-only status):
     * <p>
     * int    resId
     * UTF    name
     * UTF    phone
     * int    guests
     * UTF    date
     * UTF    time
     * UTF    request
     * UTF    createdAt
     * UTF    username
     * int    userId
     * UTF    status
     * UTF    statusUpdatedAt
     */
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

        public final String status;
        public final String statusUpdatedAt;

        public ReservationRecord(
                int resId,
                String name,
                String phone,
                int guests,
                String date,
                String time,
                String request,
                String createdAt,
                String username,
                int userId,
                String status,
                String statusUpdatedAt
        ) {
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
                    resId,
                    name,
                    phone,
                    guests,
                    date,
                    time,
                    request,
                    createdAt,
                    Session.getCurrentUsername(),
                    Session.getCurrentUserId(),
                    "Pending",
                    createdAt
            );

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATIONS_FILE, true))) {
                writeNewFormat(dos, rec);
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
            while (dis.available() > 0) {
                list.add(readNewFormat(dis));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("Reservation load error: " + e.getMessage());
        }

        return list;
    }

    /**
     * Updates ONLY the clicked reservation (by resId).
     * Works only once: once Accepted or Rejected, cannot change to the other.
     * Rewrites reservations.dat so status is stored in the same file (latest-only).
     *
     * @return true if update applied (or idempotent), false if forbidden or missing.
     */
    public static boolean setReservationStatus(int resId, String status) {
        StorageInit.ensureDataDir();

        List<ReservationRecord> all = loadReservations();
        boolean found = false;
        int matches = 0;

        for (int i = 0; i < all.size(); i++) {
            ReservationRecord r = all.get(i);
            if (r.resId != resId) continue;

            matches++;
            found = true;

            String current = (r.status == null || r.status.isBlank()) ? "Pending" : r.status;

            // Final decision: only allow idempotent "same status" updates.
            if (!"Pending".equals(current)) {
                if (current.equals(status)) {
                    return true; // idempotent
                }
                return false; // forbidden (work only once)
            }

            all.set(i, new ReservationRecord(
                    r.resId,
                    r.name,
                    r.phone,
                    r.guests,
                    r.date,
                    r.time,
                    r.request,
                    r.createdAt,
                    r.username,
                    r.userId,
                    status,
                    Instant.now().toString()
            ));
            break; // update ONLY one record
        }

        if (matches > 1) {
            System.err.println("Warning: duplicate resId found in reservations.dat: " + resId +
                    ". Only the first match was updated.");
        }

        if (!found) return false;

        return rewriteAllNewFormat(all);
    }

    public static String getReservationStatus(int resId) {
        StorageInit.ensureDataDir();
        for (ReservationRecord r : loadReservations()) {
            if (r.resId == resId) return (r.status == null || r.status.isBlank()) ? "Pending" : r.status;
        }
        return "Pending";
    }

    // -------------------- Low-level read/write (NEW FORMAT) --------------------

    private static boolean rewriteAllNewFormat(List<ReservationRecord> list) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.RESERVATIONS_FILE, false))) {
            for (ReservationRecord r : list) {
                writeNewFormat(dos, r);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Reservation rewrite error: " + e.getMessage());
            return false;
        }
    }

    private static void writeNewFormat(DataOutputStream dos, ReservationRecord r) throws IOException {
        dos.writeInt(r.resId);
        dos.writeUTF(nullToEmpty(r.name));
        dos.writeUTF(nullToEmpty(r.phone));
        dos.writeInt(r.guests);
        dos.writeUTF(nullToEmpty(r.date));
        dos.writeUTF(nullToEmpty(r.time));
        dos.writeUTF(nullToEmpty(r.request));
        dos.writeUTF(nullToEmpty(r.createdAt));
        dos.writeUTF(nullToEmpty(r.username));
        dos.writeInt(r.userId);
        dos.writeUTF(nullToEmpty(r.status.isBlank() ? "Pending" : r.status));
        dos.writeUTF(nullToEmpty(r.statusUpdatedAt));
    }

    private static ReservationRecord readNewFormat(DataInputStream dis) throws IOException {
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

        if (status == null || status.isBlank()) status = "Pending";
        if (statusUpdatedAt == null || statusUpdatedAt.isBlank()) statusUpdatedAt = createdAt;

        return new ReservationRecord(
                resId,
                name,
                phone,
                guests,
                date,
                time,
                request,
                createdAt,
                username,
                userId,
                status,
                statusUpdatedAt
        );
    }

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
}