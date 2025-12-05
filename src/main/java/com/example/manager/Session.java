package com.example.manager;

import com.example.menu.MenuClient;

import java.io.IOException;

public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "guest@gmail.com";
    private static String currentPassword = "ai01*2#";
    private static boolean isAdmin = false; // new flag for admin control

    // ===== NEW: Global menu socket client =====
    private static MenuClient menuClient;

    public static void initializeSocket() {
        if (menuClient == null) {
            menuClient = new MenuClient(); // connect to server
        }
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
        isAdmin = false; // normal users are never admins
    }

    public static void setAdminUser() throws IOException {
        currentUsername = "admin";
        currentUserId = Integer.parseInt(AdminFileStorage.ADMIN_ID);
        currentEmail = "admin@munchoak.com"; // optional
        currentPassword = AdminFileStorage.getAdminPassword(); // <-- new getter needed
        isAdmin = true; // set admin flag
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "guest@munchoak.com";
        currentPassword = "guestPass#123";
        isAdmin = false;
    }

    public static void logout() {
        resetToGuest();
    }
}
