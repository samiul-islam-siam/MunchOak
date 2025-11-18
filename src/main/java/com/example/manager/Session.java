
package com.example.manager;

public class Session {
    private static int currentUserId = 2025000;
    private static String currentUsername = "guest";
    private static String currentEmail = "people@gmail.com";
    private static String currentPassword = "ai01*2#";

    // GETTERS
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

    // Set the currently logged-in user by username
    public static void setCurrentUser(String username) {
        currentUsername = username;
        currentUserId = FileStorage.getUserId(username);
        currentEmail = FileStorage.getUserEmail(username);
        currentPassword = FileStorage.getUserPassword(username);
    }

    // Role based on username
    public static String getCurrentRole() {
        return "admin".equals(currentUsername) ? "ADMIN" : "USER";
    }

    // OPTIONAL: Reset to guest
    public static void resetToGuest() {
        setCurrentUser("guest");
    }
}
