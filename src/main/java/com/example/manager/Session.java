package com.example.manager;

import com.example.menu.MenuClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "guest@gmail.com";
    private static String currentPassword = "ai01*2#";
    private static boolean isAdmin = false; // new flag for admin control

    // ===== NEW: Global menu socket client =====
    private static MenuClient menuClient;
    private static Runnable reservationListener;

    public static void setReservationListener(Runnable r) {
        reservationListener = r;
    }

    public static void notifyReservationUpdated() {
        if (reservationListener != null) {
            reservationListener.run();
        }
    }

    public static void initializeSocket() {
        if (menuClient == null) {
            menuClient = new MenuClient(); // connect to server
        }
    }
    private static String currentContactNo = "N/A"; // default

    public static String getCurrentContactNo() {
        return currentContactNo;
    }

    public static void setCurrentContactNo(String contactNo) {
        currentContactNo = contactNo;
    }

    public static void setMenuClient(MenuClient client) {
        menuClient = client;
    }

    public static MenuClient getMenuClient() {
        return menuClient;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

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

    }

    public static void setAdminUser() throws IOException {
        currentUsername = "admin";
        currentUserId = Integer.parseInt(AdminFileStorage.ADMIN_ID);
        currentEmail = "admin@munchoak.com"; // optional
        currentContactNo = "N/A";

        currentPassword = AdminFileStorage.getAdminPassword(); // <-- new getter needed
        isAdmin = true; // set admin flag
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "guest@munchoak.com";
        currentContactNo = "00000000000";

        currentPassword = "guestPass#123";
        currentUserId = 2025000;
       // currentContactNo = "00000000000";

        isAdmin = false;
    }

    public static void logout() {
        resetToGuest();
    }
    private static final List<Runnable> messageListeners = new ArrayList<>();

    public static void addMessageListener(Runnable r) {
        messageListeners.add(r);
    }

    public static void notifyMessageUpdated() {
        for (Runnable r : messageListeners) r.run();
    }

}
