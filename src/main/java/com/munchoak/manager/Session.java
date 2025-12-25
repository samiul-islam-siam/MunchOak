package com.munchoak.manager;

import com.munchoak.server.MainClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "N/A";
    private static String currentPassword = "N/A";
    private static String currentContactNo = "N/A"; // default
    private static boolean isAdmin = false; // new flag for admin control
    private static boolean isGuest = true;
    private static final List<Runnable> couponListeners = new ArrayList<>();
    public static void addCouponListener(Runnable r) {
        couponListeners.add(r);
    }
    public static void notifyCouponUpdated() {
        for (Runnable r : couponListeners) {
            r.run();
        }
    }

    public static boolean isGuest() {
        return isGuest;
    }


    private static MainClient mainClient;
    private static Runnable reservationListener;
    private static final List<Runnable> messageListeners = new ArrayList<>();


    public static void setReservationListener(Runnable r) {
        reservationListener = r;
    }

    public static void notifyReservationUpdated() {
        if (reservationListener != null) {
            reservationListener.run();
        }
    }

    public static void addMessageListener(Runnable r) {
        messageListeners.add(r);
    }

    public static void notifyMessageUpdated() {
        for (Runnable r : messageListeners) r.run();
    }

    // Contact No get-set pair
    public static String getCurrentContactNo() {
        return currentContactNo;
    }

    public static void setCurrentContactNo(String contactNo) {
        currentContactNo = contactNo;
    }

    // Email get-set pair
    public static String getCurrentEmail() {
        return currentEmail;
    }

    public static void setCurrentEmail(String email) {
        currentEmail = email;
    }

    // Menu client get-set pair
    public static MainClient getMenuClient() {
        return mainClient;
    }

    public static void setMenuClient(MainClient client) {
        mainClient = client;
    }

    // User ID get-set pair
    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    // Username get-set pair
    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    // Password get-set pair
    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static void setCurrentPassword(String password) {
        currentPassword = password;
    }

    // --------------------LOGIN VALIDATION-----------------------------------
    public static boolean isAdmin() {
        return isAdmin;
    }

    public static String getCurrentRole() {
        return isAdmin ? "ADMIN" : "USER";
    }

    public static void setCurrentUser(String username) {
        currentUsername = username;
        currentUserId = UserStorage.getUserId(username);
        currentEmail = UserStorage.getUserEmail(username);
        currentPassword = UserStorage.getUserPassword(username);
        currentContactNo = UserStorage.getUserContact(username);
        isAdmin = false; // normal users are never admins
        isGuest = false;
    }

    public static void setAdminUser() throws IOException {

        currentUsername = "admin";
        currentUserId = Integer.parseInt(AdminStorage.ADMIN_ID);
        currentEmail = "admin@munchoak.com"; // optional
        currentContactNo = "N/A";
        currentPassword = AdminStorage.getAdminPassword();
        isAdmin = true; // set admin flag
        isGuest = false;

    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "N/A";
        currentContactNo = "N/A";
        currentPassword = "N/A";
        currentUserId = 2025000;
        isAdmin = false;
        isGuest = true;
    }

    public static void refreshAdminFromFile() throws IOException {
        List<String> lines = AdminStorage.readLines();
        boolean found = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(AdminStorage.ADMIN_ID) && parts.length >= 5) {
                currentUserId = Integer.parseInt(parts[0]);
                currentUsername = parts[1];
                currentEmail = parts[2];
                currentContactNo = parts[3];
                currentPassword = parts[4];
                isAdmin = true;
                isGuest = false;
                found = true;
                break;
            }
        }

        // Only fallback if admin.dat had no valid line
        if (!found) {
            currentUserId = Integer.parseInt(AdminStorage.ADMIN_ID);
            currentUsername = "admin";
            currentEmail = "admin@munchoak.com";
            currentContactNo = "N/A";
            currentPassword = "N/A";
            isAdmin = true;
            isGuest = false;
        }
    }

    public static void logout() {
        resetToGuest();
    }
}
