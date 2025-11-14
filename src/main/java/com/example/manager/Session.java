package com.example.manager;

public class Session {
    private static int currentUserId = 2025000;  // Default user (for now)
    private static String currentUsername = "guest";

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String name) {
        currentUsername = name;
    }

    public static String getCurrentRole() {
        if (currentUsername.equals("admin")) {
            return "ADMIN";
        } else {
            return "USER";
        }
    }
}