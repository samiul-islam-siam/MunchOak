
package com.example.manager;

public class Session {

    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "guest@gmail.com";
    private static String currentPassword = "ai01*2#";

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

    public static void setCurrentUser(String username) {
        currentUsername = username;
        currentUserId = FileStorage.getUserId(username);
        currentEmail = FileStorage.getUserEmail(username);
        currentPassword = FileStorage.getUserPassword(username);
    }

    public static String getCurrentRole() {
        if ("admin".equalsIgnoreCase(currentUsername)) return "ADMIN";
        if ("guest".equalsIgnoreCase(currentUsername)) return "GUEST";
        return "USER";
    }

    public static void resetToGuest() {
        currentUsername = "guest";
        currentEmail = "guest@gmail.com";
        currentPassword = "ai01*2#";
    }

    public static void logout() {
        resetToGuest();
    }
}
