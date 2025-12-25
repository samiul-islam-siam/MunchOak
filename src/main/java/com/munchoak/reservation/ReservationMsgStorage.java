package com.munchoak.reservation;

import com.munchoak.manager.StorageInit;
import com.munchoak.manager.StoragePaths;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class ReservationMsgStorage {
    private ReservationMsgStorage() {}

    public static class MessageRecord {
        public final int userId;
        public final String sender;
        public final String message;
        public final String timestamp;

        public MessageRecord(int userId, String sender, String message, String timestamp) {
            this.userId = userId;
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    public static List<MessageRecord> loadMessagesForUser(int userId) {
        StorageInit.ensureDataDir();
        List<MessageRecord> list = new ArrayList<>();
        if (!StoragePaths.NOTIFICATIONS_FILE.exists() || StoragePaths.NOTIFICATIONS_FILE.length() == 0) return list;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.NOTIFICATIONS_FILE))) {
            while (dis.available() > 0) {
                int uid = dis.readInt();
                String sender = dis.readUTF();
                String msg = dis.readUTF();
                String time = dis.readUTF();
                if (uid == userId) list.add(new MessageRecord(uid, sender, msg, time));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("Notification load error: " + e.getMessage());
        }
        return list;
    }

    public static void saveMessageForUser(int userId, String sender, String message) {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.NOTIFICATIONS_FILE, true))) {
            dos.writeInt(userId);
            dos.writeUTF(sender);
            dos.writeUTF(message);
            dos.writeUTF(Instant.now().toString());
        } catch (IOException e) {
            System.err.println("Notification save error: " + e.getMessage());
        }
    }
}