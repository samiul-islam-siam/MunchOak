package com.example.manager;

import com.example.menu.MenuClient;

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
    public static boolean isGuest()
    {
        return isGuest;
    }


    private static MenuClient menuClient;
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
    public static MenuClient getMenuClient() {
        return menuClient;
    }

    public static void setMenuClient(MenuClient client) {
        menuClient = client;
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
        currentUserId = FileStorage.getUserId(username);
        currentEmail = FileStorage.getUserEmail(username);
        currentPassword = FileStorage.getUserPassword(username);
        currentContactNo = FileStorage.getUserContact(username);
        isAdmin = false; // normal users are never admins
        isGuest = false;
    }

    public static void setAdminUser() throws IOException {
        currentUsername = "admin";
        currentUserId = Integer.parseInt(AdminFileStorage.ADMIN_ID);
        currentEmail = "admin@munchoak.com"; // optional
        currentContactNo = "N/A";
        currentPassword = AdminFileStorage.getAdminPassword();
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

    public static void logout() {
        resetToGuest();
    }
}
